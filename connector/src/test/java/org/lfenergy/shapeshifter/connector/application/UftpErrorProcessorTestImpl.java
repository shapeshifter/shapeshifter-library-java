package org.lfenergy.shapeshifter.connector.application;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.UftpErrorProcessor;
import org.springframework.stereotype.Component;

@Component
public class UftpErrorProcessorTestImpl implements UftpErrorProcessor {


  @Override
  public void duplicateReceived(UftpParticipant senderInformation, PayloadMessageType payloadMessage) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void onErrorDuringReceivedMessageReading(String transportXml, Exception errorCause) {
    throw new RuntimeException(errorCause);
  }

  @Override
  public void onErrorDuringReceivedMessageProcessing(PayloadMessageType payloadMessage, Exception errorCause) {
    throw new RuntimeException(errorCause);
  }

  @Override
  public void onReceivedMessageUftpValidationRejection(UftpParticipant sender, PayloadMessageType payloadMessage, String rejectionReason) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
