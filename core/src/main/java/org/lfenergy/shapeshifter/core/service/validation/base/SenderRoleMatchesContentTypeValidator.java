// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.model.UftpRoleInformation;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;


@RequiredArgsConstructor
public class SenderRoleMatchesContentTypeValidator implements UftpValidator<PayloadMessageType> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return true;
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_BASE;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    return contentMatchesRole(uftpMessage.sender(), uftpMessage.payloadMessage());
  }

  private boolean contentMatchesRole(UftpParticipant sender, PayloadMessageType payloadMessage) {
    return UftpRoleInformation.getMessageTypes(sender.role())
                              .contains(payloadMessage.getClass());
  }

  @Override
  public String getReason() {
    return "Invalid Sender (not matching role)";
  }
}
