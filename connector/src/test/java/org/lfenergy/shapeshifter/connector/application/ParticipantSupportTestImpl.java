// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.application;

import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.ParticipantSupport;
import org.springframework.stereotype.Component;

@Component
public class ParticipantSupportTestImpl implements ParticipantSupport {

  @Override
  public boolean isHandledRecipient(UftpParticipant recipient) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isAllowedSender(UftpParticipant sender) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
