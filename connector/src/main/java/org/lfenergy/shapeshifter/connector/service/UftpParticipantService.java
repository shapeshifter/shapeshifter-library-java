// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service;

import java.util.Optional;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.api.model.UftpParticipantInformation;

/**
 * Provide a UftpParticipantService bean in your application in order to - be able to verify an incoming message with the sender's public key - be able to connect to the sender's
 * endpoint
 *
 * <pre>
 * @Service
 * public class MyUftpParticipantService implements UftpParticipantService {
 *   // ...
 * }
 * </pre>
 */
public interface UftpParticipantService {

  /**
   * Method that will give the public key and endpoint from a sender of a message
   *
   * @param role The sender's type of organisation in conversations
   * @param domain The sender's domain
   * @return The sender's public key and endpoint
   */
  Optional<UftpParticipantInformation> getParticipantInformation(USEFRoleType role, String domain);

}
