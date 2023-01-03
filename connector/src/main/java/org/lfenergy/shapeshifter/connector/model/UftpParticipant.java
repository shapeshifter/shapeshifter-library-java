package org.lfenergy.shapeshifter.connector.model;

import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;

public record UftpParticipant(String domain, USEFRoleType role) {

  public UftpParticipant(SignedMessage signedMessage) {
    this(signedMessage.getSenderDomain(), signedMessage.getSenderRole());
  }
}
