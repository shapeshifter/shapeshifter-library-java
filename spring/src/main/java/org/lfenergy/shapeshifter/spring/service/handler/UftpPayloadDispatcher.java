// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.service.handler;

import java.util.List;
import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.common.HttpStatusCode;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.handler.UftpPayloadHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@link UftpPayloadHandler} that takes any incoming or outgoing UFTP message and dispatches it to one or more appropriate registered handlers.
 */
@CommonsLog
@Component
class UftpPayloadDispatcher implements UftpPayloadHandler {

  private final List<UftpIncomingHandler<? extends PayloadMessageType>> incomingHandlers;
  private final List<UftpOutgoingHandler<? extends PayloadMessageType>> outgoingHandlers;

  /**
   * Initializes the dispatcher with the given incoming and outgoing handlers.
   *
   * @param incomingHandlers The incoming handlers to register.
   * @param outgoingHandlers The outgoing handlers to register.
   */
  @Autowired
  public UftpPayloadDispatcher(List<UftpIncomingHandler<? extends PayloadMessageType>> incomingHandlers,
                               List<UftpOutgoingHandler<? extends PayloadMessageType>> outgoingHandlers) {
    this.incomingHandlers = incomingHandlers;
    this.outgoingHandlers = outgoingHandlers;

    log.info(String.format("Registered UFTP incoming handlers: %s", incomingHandlers));
    log.info(String.format("Registered UFTP outgoing handlers: %s", outgoingHandlers));
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void notifyNewIncomingMessage(UftpParticipant from, PayloadMessageType message) {
    var messageType = message.getClass();

    log.debug(String.format("Notifying application handler of incoming %s message from %s", messageType.getSimpleName(), from));

    var matchingHandlers = incomingHandlers.stream()
                                           .filter(incomingHandler -> incomingHandler.isSupported(messageType))
                                           .toList();

    if (matchingHandlers.isEmpty()) {
      throw new UftpConnectorException("No incoming handler for message type: " + messageType.getSimpleName(), HttpStatusCode.NOT_IMPLEMENTED);
    }

    for (var handler : matchingHandlers) {
      ((UftpIncomingHandler) handler).handle(from, message);
    }
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void notifyNewOutgoingMessage(UftpParticipant from, PayloadMessageType message) {
    var messageType = message.getClass();

    log.debug(String.format("Notifying application handler of outgoing %s message from %s", messageType.getSimpleName(), from));

    var matchingHandlers = outgoingHandlers.stream()
                                           .filter(outgoingHandler -> outgoingHandler.isSupported(messageType))
                                           .toList();

    if (matchingHandlers.isEmpty()) {
      throw new UftpConnectorException("No outgoing handler for message type: " + messageType.getSimpleName());
    }

    for (var handler : matchingHandlers) {
      ((UftpOutgoingHandler) handler).handle(from, message);
    }
  }

}
