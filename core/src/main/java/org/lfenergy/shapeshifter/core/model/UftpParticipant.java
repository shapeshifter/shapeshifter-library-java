// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.model;

import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;

/**
 * UFTP details of your organisation as a participant in conversations
 *
 * @param domain The <code>SenderDomain</code> or <code>RecipientDomain</code> from the Shapeshifter specifications
 * @param role The type of participant your organisation is in conversations
 */
public record UftpParticipant(String domain, USEFRoleType role) {

  public UftpParticipant(SignedMessage signedMessage) {
    this(signedMessage.getSenderDomain(), signedMessage.getSenderRole());
  }
}
