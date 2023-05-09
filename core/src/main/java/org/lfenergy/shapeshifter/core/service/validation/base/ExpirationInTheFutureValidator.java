// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import java.time.OffsetDateTime;
import java.util.Map;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.tools.PayloadMessagePropertyRetriever;


public class ExpirationInTheFutureValidator implements UftpValidator<PayloadMessageType> {

  private final PayloadMessagePropertyRetriever<PayloadMessageType, OffsetDateTime> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexRequest.class, m -> ((FlexRequest) m).getExpirationDateTime(),
          FlexOffer.class, m -> ((FlexOffer) m).getExpirationDateTime()
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.isTypeInMap(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    var value = retriever.getOptionalProperty(uftpMessage.payloadMessage());
    return value.isEmpty() || isInFuture(value.get());
  }

  private boolean isInFuture(OffsetDateTime expirationMoment) {
    var now = OffsetDateTime.now();
    return expirationMoment.isAfter(now);
  }

  @Override
  public String getReason() {
    return "ExpirationDateTime out of bounds";
  }
}
