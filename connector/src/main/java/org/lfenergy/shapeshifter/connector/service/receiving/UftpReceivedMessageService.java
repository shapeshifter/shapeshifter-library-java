// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.receiving;

import static org.lfenergy.shapeshifter.connector.model.UftpRoleInformation.getRecipientRoleBySenderRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.config.UftpConnectorConfig;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.UftpPayloadHandler;
import org.lfenergy.shapeshifter.connector.service.receiving.response.UftpValidationResponseCreator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.connector.service.validation.model.ValidationResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UftpReceivedMessageService {

  private final UftpConnectorConfig config;
  private final UftpValidationService validator;
  private final UftpPayloadHandler payloadHandler;

  /**
   * Processes an incoming flex message in a specific conversation. Can be called from within your own incoming flex message processor. When the message passed validation, a
   * response message is created and sent.
   *
   * <pre>
   * public void process(String senderDomain, USEFRoleType role, PayloadMessageType payloadMessage) {
   *   var from = new UftpParticipant(senderDomain, role);
   *   var validationResult = uftpReceivedMessageService.process(from, payloadMessage);
   *
   *   // implement further business logic here
   * }
   * </pre>
   *
   * @param from The company uftp details of the recipient
   * @param payloadMessage The details of the flex message, including messageID and conversationID
   * @return The validation result, either `ok` or `rejected` including a rejection reason
   */
  public ValidationResult process(UftpParticipant from, PayloadMessageType payloadMessage) {
    var validationResult = validateMessage(from, payloadMessage);

    if (UftpMessage.isResponse(payloadMessage)) {
      processPayloadMessageResponse(payloadMessage, validationResult);
    } else {
      processPayloadMessage(from, payloadMessage, validationResult);
    }

    return validationResult;
  }

  private void processPayloadMessageResponse(PayloadMessageType response, ValidationResult validationResult) {
    if (!validationResult.valid()) {
      log.warn("Received invalid {} with MessageID '{}': {}.", response.getClass().getSimpleName(), response.getMessageID(), validationResult.rejectionReason());
    }
  }

  private void processPayloadMessage(UftpParticipant from, PayloadMessageType payloadMessage, ValidationResult validationResult) {
    var response = UftpValidationResponseCreator.getResponseForMessage(payloadMessage, validationResult);
    var originalRecipient = new UftpParticipant(payloadMessage.getRecipientDomain(), getRecipientRoleBySenderRole(from.role()));

    payloadHandler.notifyNewOutgoingMessage(originalRecipient, response);
  }

  private ValidationResult validateMessage(UftpParticipant from, PayloadMessageType payloadMessage) {
    if (shouldPerformValidations()) {
      return validator.validate(UftpMessage.createIncoming(from, payloadMessage));
    }
    return ValidationResult.ok();
  }

  private boolean shouldPerformValidations() {
    return config.receiving().validation().enabled();
  }
}
