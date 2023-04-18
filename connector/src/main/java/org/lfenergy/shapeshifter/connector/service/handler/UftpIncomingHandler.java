// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;

/**
 * Interface that must be implemented by the application in one or more beans to handle incoming UFTP messages.
 */
public interface UftpIncomingHandler<T extends PayloadMessageType> {

  /**
   * Returns true if the handler supports the given message type.
   */
  boolean isSupported(Class<? extends PayloadMessageType> messageType);

  /**
   * Handles the incoming message either by immediately calling {@link org.lfenergy.shapeshifter.connector.service.receiving.UftpReceivedMessageService} or by queueing the message
   * somehow for later processing.
   */
  void handle(UftpParticipant sender, T message);

}
