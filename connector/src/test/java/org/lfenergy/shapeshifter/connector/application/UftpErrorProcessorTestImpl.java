// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.application;

import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.UftpErrorProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UftpErrorProcessorTestImpl implements UftpErrorProcessor {


  @Override
  public void duplicateReceived(UftpParticipant senderInformation, PayloadMessageType payloadMessage) {
    log.warn("Received duplicate message {} from {}", payloadMessage, senderInformation);
  }

  @Override
  public void onErrorDuringReceivedMessageReading(String transportXml, Exception errorCause) {
    log.error(errorCause.getMessage());
  }

  @Override
  public void onErrorDuringReceivedMessageProcessing(PayloadMessageType payloadMessage, Exception errorCause) {
    log.error(errorCause.getMessage());
  }

  @Override
  public void onReceivedMessageUftpValidationRejection(UftpParticipant sender, PayloadMessageType payloadMessage, String rejectionReason) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
