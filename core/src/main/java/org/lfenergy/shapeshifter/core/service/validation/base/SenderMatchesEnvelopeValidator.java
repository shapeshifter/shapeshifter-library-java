// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@RequiredArgsConstructor
public class SenderMatchesEnvelopeValidator implements UftpValidator<PayloadMessageType> {

  private static final String REJECTION_REASON = "Invalid Sender (not matching envelope)";

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return true;
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_BASE;
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
    return isSameSender(uftpMessage.sender(), uftpMessage.payloadMessage());
  }

  private boolean isSameSender(UftpParticipant sender, PayloadMessageType payloadMessage) {
    return sender.domain().equals(payloadMessage.getSenderDomain());
  }

}
