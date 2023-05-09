// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;

public interface UftpErrorProcessor {

  void onErrorDuringReceivedMessageReading(String transportXml, Exception errorCause);

  void onDuplicateReceived(UftpParticipant senderInformation, PayloadMessageType payloadMessage);

  /**
   * A received message is not valid according to the UFTP protocol specification or specific application rules that have been added to the definition.
   *
   * @param sender The sender of the message.
   * @param rejectionReason The reason for the rejection.
   */
  void onReceivedMessageUftpValidationRejection(UftpParticipant sender, PayloadMessageType payloadMessage, String rejectionReason);
}
