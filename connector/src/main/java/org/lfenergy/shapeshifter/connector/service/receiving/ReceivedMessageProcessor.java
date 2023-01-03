package org.lfenergy.shapeshifter.connector.service.receiving;

import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.DUPLICATE_MESSAGE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.connector.service.forwarding.UftpPayloadForwarder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceivedMessageProcessor {

  private final UftpPayloadForwarder forwarder;
  private final DuplicateMessageDetection duplicateDetection;
  private final UftpErrorProcessor errorProcessor;

  public void onReceivedMessage(SignedMessage signedMessage, PayloadMessageType request) {
    var sender = new UftpParticipant(signedMessage);
    log.debug("Processing of received {} message from {}", request.getClass().getSimpleName(), sender);

    if (isDuplicateMessage(sender, request)) {
      return;
    }

    forwarder.notifyNewIncomingMessage(sender, request);
  }

  private boolean isDuplicateMessage(UftpParticipant sender, PayloadMessageType payloadMessage) {
    try {
      var duplicate = duplicateDetection.isDuplicate(payloadMessage) == DUPLICATE_MESSAGE;
      if (duplicate) {
        log.info("Received message {} {} from {} is a duplicate and already processed. It will not be submitted to the application.",
                 payloadMessage.getClass(), payloadMessage.getMessageID(), sender);
        errorProcessor.duplicateReceived(sender, payloadMessage);
      }
      return duplicate;
    } catch (Exception ex) {
      var newEx = new UftpConnectorException("Exception during processing of " + payloadMessage.getClass().getSimpleName() + "; could not determine whether this message was already submitted", ex);
      errorProcessor.onErrorDuringReceivedMessageProcessing(payloadMessage, newEx);
      throw newEx;
    }
  }
}
