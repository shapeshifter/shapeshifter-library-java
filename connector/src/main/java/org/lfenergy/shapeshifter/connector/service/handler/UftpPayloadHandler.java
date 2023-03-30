// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UftpPayloadHandler {

  private final UftpHandlerMapping handlerMapping;

  public <T extends PayloadMessageType> void notifyNewIncomingMessage(UftpParticipant from, T message) {
    log.debug("Notifying application of incoming {} message handler from {}", message.getClass(), from);
    var handlerMethod = handlerMapping.findIncomingHandler(message.getClass());
    if (handlerMethod.isEmpty()) {
      throw new UftpConnectorException("No incoming handler method found for message type: " + message.getClass().getName());
    }
    handleMessage(handlerMethod.get(), from, message);
  }

  public <T extends PayloadMessageType> void notifyNewOutgoingMessage(UftpParticipant from, T message) {
    log.debug("Notifying application of outgoing {} message handler from {}", message.getClass(), from);
    var handlerMethod = handlerMapping.findOutgoingHandler(message.getClass());
    if (handlerMethod.isEmpty()) {
      throw new UftpConnectorException("No outgoing handler method found for message type: " + message.getClass().getName());
    }
    handleMessage(handlerMethod.get(), from, message);
  }

  private <T extends PayloadMessageType> void handleMessage(UftpHandlerMethod handlerMethod, UftpParticipant from, T message) {
    try {
      handlerMethod.method().invoke(handlerMethod.bean(), from, message);
    } catch (Exception cause) {
      throw new UftpConnectorException("Exception during processing of message of type: " + message.getClass().getSimpleName(), cause);
    }
  }
}
