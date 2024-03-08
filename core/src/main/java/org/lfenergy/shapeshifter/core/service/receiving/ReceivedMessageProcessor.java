// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.receiving;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.core.service.handler.UftpPayloadHandler;
import org.lfenergy.shapeshifter.core.service.receiving.DuplicateMessageDetection.DuplicateMessageResult;

/**
 * This is an internal API and should not be used by applications. It may change or be removed in a future release.
 */
@CommonsLog
@RequiredArgsConstructor
public class ReceivedMessageProcessor {

  private final UftpPayloadHandler payloadHandler;
  private final DuplicateMessageDetection duplicateDetection;
  private final UftpErrorProcessor errorProcessor;

  public void onReceivedMessage(IncomingUftpMessage<? extends PayloadMessageType> message) {
    var request = message.payloadMessage();
    var sender = message.sender();
    log.debug(String.format("Processing of received %s message from %s", request.getClass().getSimpleName(), sender));

    if (isDuplicateMessage(sender, request)) {
      throw new DuplicateMessageException(
          "Received " + request.getClass().getSimpleName() + " with MessageID '" + request.getMessageID() + "' from " + sender + " is a duplicate and has already been received.");
    }

    payloadHandler.notifyNewIncomingMessage(message);
  }

  private boolean isDuplicateMessage(UftpParticipant sender, PayloadMessageType payloadMessage) {
    var duplicate = duplicateDetection.isDuplicate(payloadMessage) != DuplicateMessageResult.NEW_MESSAGE;
    if (duplicate) {
      log.info(String.format("Received message %s (%s) from %s is a duplicate and has already been processed. It will not be submitted to the application.",
                             payloadMessage.getClass(), payloadMessage.getMessageID(), sender));

      errorProcessor.onDuplicateReceived(sender, payloadMessage);
    }
    return duplicate;
  }
}
