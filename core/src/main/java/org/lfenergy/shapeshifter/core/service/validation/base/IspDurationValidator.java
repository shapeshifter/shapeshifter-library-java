// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;
import org.lfenergy.shapeshifter.core.service.validation.tools.PayloadMessagePropertyRetriever;

@RequiredArgsConstructor
public class IspDurationValidator implements UftpValidator<PayloadMessageType> {

  private static final String REJECTION_REASON = "ISP duration rejected";

  private final UftpValidatorSupport support;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, Duration> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexMessageType.class, m -> ((FlexMessageType) m).getISPDuration(),
          Metering.class, m -> ((Metering) m).getISPDuration()
      )
  );

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.isTypeInMap(clazz);
  }

  @Override
  public ValidationResult validate(UftpMessage<PayloadMessageType> uftpMessage) {
    return ValidationResult.of(isMessageValid(uftpMessage), REJECTION_REASON);
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    return isMessageValid(uftpMessage);
  }

  @Override
  public String getReason() {
    return REJECTION_REASON;
  }

  private boolean isMessageValid(UftpMessage<PayloadMessageType> uftpMessage) {
    var value = retriever.getOptionalProperty(uftpMessage.payloadMessage());
    return value.isEmpty() || support.isSupportedIspDuration(value.get());
  }

}
