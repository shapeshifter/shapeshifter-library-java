package org.lfenergy.shapeshifter.connector.service;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;

public interface UftpErrorProcessor {

  void onErrorDuringReceivedMessageReading(String transportXml, Exception errorCause);

  void onErrorDuringReceivedMessageProcessing(PayloadMessageType payloadMessage, Exception errorCause);

  void duplicateReceived(UftpParticipant senderInformation, PayloadMessageType payloadMessage);

  /**
   * A received message is not valid according to the UFTP protocol specification or specific application rules that have been added to the definition.
   *
   * @param sender The sender of the message.
   * @param rejectionReason The reason for the rejection.
   */
  void onReceivedMessageUftpValidationRejection(UftpParticipant sender, PayloadMessageType payloadMessage, String rejectionReason);
}
