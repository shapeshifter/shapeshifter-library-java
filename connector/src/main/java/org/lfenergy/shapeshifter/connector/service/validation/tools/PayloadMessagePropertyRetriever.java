// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.tools;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;

@Slf4j
public class PayloadMessagePropertyRetriever<T extends PayloadMessageType, R> {

  private final Map<Class<? extends PayloadMessageType>, Function<PayloadMessageType, R>> typePropertyGetterMap;

  public PayloadMessagePropertyRetriever(Map<Class<? extends PayloadMessageType>, Function<PayloadMessageType, R>> typePropertyGetterMap) {
    this.typePropertyGetterMap = typePropertyGetterMap;
  }

  public boolean isTypeInMap(Class<? extends PayloadMessageType> validatedMsgType) {
    return findApplicableKey(validatedMsgType).isPresent();
  }

  public Optional<R> getOptionalProperty(T payloadMessage) {
    var getter = retrieveProperetyGetter(payloadMessage);
    var result = getter.apply(payloadMessage);
    return Optional.ofNullable(result);
  }

  public R getProperty(T payloadMessage) {
    var result = getOptionalProperty(payloadMessage);
    if (result.isEmpty()) {
      throw new UftpConnectorException("Lambda returned null for: " + payloadMessage.getClass());
    }
    return result.get();
  }

  private Function<PayloadMessageType, R> retrieveProperetyGetter(T payloadMessage) {
    var key = getApplicableKey(payloadMessage.getClass());
    return typePropertyGetterMap.get(key);
  }

  private Class<? extends PayloadMessageType> getApplicableKey(Class<? extends PayloadMessageType> validatedMsgType) {
    return findApplicableKey(validatedMsgType)
        .orElseThrow(() -> new UftpConnectorException("Unexpected payload message type in validation: " + validatedMsgType));
  }

  private Optional<Class<? extends PayloadMessageType>> findApplicableKey(Class<? extends PayloadMessageType> validatedMsgType) {
    return typePropertyGetterMap
        .keySet().stream()
        .filter(appliesToType -> appliesToType.isAssignableFrom(validatedMsgType))
        .findFirst();
  }
}
