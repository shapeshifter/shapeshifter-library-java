// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class UftpPayloadHandler {

  private final List<UftpIncomingHandler<? extends PayloadMessageType>> incomingHandlers;
  private final List<UftpOutgoingHandler<? extends PayloadMessageType>> outgoingHandlers;

  @Autowired
  public UftpPayloadHandler(List<UftpIncomingHandler<? extends PayloadMessageType>> incomingHandlers,
                            List<UftpOutgoingHandler<? extends PayloadMessageType>> outgoingHandlers) {
    this.incomingHandlers = incomingHandlers;
    this.outgoingHandlers = outgoingHandlers;

    log.info("Discovered UFTP incoming handlers: {}", incomingHandlers);
    log.info("Discovered UFTP outgoing handlers: {}", outgoingHandlers);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void notifyNewIncomingMessage(UftpParticipant from, PayloadMessageType message) {
    var messageType = message.getClass();

    log.debug("Notifying application handler of incoming {} message from {}", messageType.getSimpleName(), from);

    var matchingHandlers = incomingHandlers.stream()
                                           .filter(incomingHandler -> incomingHandler.isSupported(messageType))
                                           .toList();

    if (matchingHandlers.isEmpty()) {
      throw new UftpConnectorException("No incoming handler for message type: " + messageType.getSimpleName(), HttpStatus.NOT_IMPLEMENTED);
    }

    for (var handler : matchingHandlers) {
      ((UftpIncomingHandler) handler).handle(from, message);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void notifyNewOutgoingMessage(UftpParticipant from, PayloadMessageType message) {
    var messageType = message.getClass();

    log.debug("Notifying application handler of outgoing {} message from {}", messageType.getSimpleName(), from);

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
