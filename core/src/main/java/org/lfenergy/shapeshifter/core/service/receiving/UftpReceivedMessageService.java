// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.receiving;

import static org.lfenergy.shapeshifter.core.model.UftpRoleInformation.getRecipientRoleBySenderRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.handler.UftpPayloadHandler;
import org.lfenergy.shapeshifter.core.service.receiving.response.UftpValidationResponseCreator;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@Slf4j
@RequiredArgsConstructor
public class UftpReceivedMessageService {

  private final UftpValidationService validationService;
  private final UftpPayloadHandler payloadHandler;

  private boolean shouldPerformValidations = true;

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
    if (shouldPerformValidations) {
      return validationService.validate(UftpMessage.createIncoming(from, payloadMessage));
    }
    return ValidationResult.ok();
  }

  public boolean getShouldPerformValidations() {
    return shouldPerformValidations;
  }

  public void setShouldPerformValidations(boolean shouldPerformValidations) {
    this.shouldPerformValidations = shouldPerformValidations;
  }
}
