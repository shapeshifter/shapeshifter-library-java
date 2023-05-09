// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.service.handler;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;

/**
 * Interface that must be implemented by the application in one or more beans to handle outgoing UFTP messages.
 */
public interface UftpOutgoingHandler<T extends PayloadMessageType> {

  /**
   * Returns true if the handler supports the given message type.
   */
  boolean isSupported(Class<? extends PayloadMessageType> messageType);

  /**
   * Handles the outgoing message either by immediately calling {@link org.lfenergy.shapeshifter.core.service.sending.UftpSendMessageService} or by queueing the message
   * somehow for later sending.
   */
  void handle(UftpParticipant sender, T message);

}
