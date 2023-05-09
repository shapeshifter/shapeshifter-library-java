// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.lfenergy.shapeshifter.core.model.UftpRoleInformation.getRecipientRoleBySenderRole;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.ParticipantSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;


@RequiredArgsConstructor
public class RecipientValidator implements UftpValidator<PayloadMessageType> {

  private final ParticipantSupport participantSupport;

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
    var recipient = new UftpParticipant(uftpMessage.payloadMessage().getRecipientDomain(), getRecipientRoleBySenderRole(uftpMessage.sender().role()));
    return participantSupport.isHandledRecipient(recipient);
  }

  @Override
  public String getReason() {
    return "Unknown Recipient";
  }
}
