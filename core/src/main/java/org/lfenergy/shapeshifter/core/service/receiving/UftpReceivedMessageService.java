// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.receiving;

import static org.lfenergy.shapeshifter.core.model.UftpRoleInformation.getRecipientRoleBySenderRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.handler.UftpPayloadHandler;
import org.lfenergy.shapeshifter.core.service.receiving.response.UftpValidationResponseCreator;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@CommonsLog
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
   * @deprecated This method will be removed in the future. Use {@link #process(IncomingUftpMessage)} instead.
   */
  @Deprecated(forRemoval = true, since = "2.3.0")
  public ValidationResult process(UftpParticipant from, PayloadMessageType payloadMessage) {
    return process(IncomingUftpMessage.create(from, payloadMessage, null, null));
  }

  /**
   * Processes an incoming flex message in a specific conversation. Can be called from within your own incoming flex message processor. When the message passed validation, a
   * response message is created and sent.
   *
   * <pre><code>
   * public void process(IncomingUftpMessage&lt;? extends PayloadMessageType&gt; message) {
   *   var validationResult = uftpReceivedMessageService.process(message);
   *
   *   // implement further business logic here
   * }
   * </code></pre>
   *
   * @param uftpMessage The flex message
   * @return The validation result, either `ok` or `rejected` including a rejection reason
   */
  public <T extends PayloadMessageType> ValidationResult process(IncomingUftpMessage<T> uftpMessage) {
    var validationResult = validateMessage(uftpMessage);

    var payloadMessage = uftpMessage.payloadMessage();
    if (UftpMessage.isResponse(payloadMessage)) {
      processPayloadMessageResponse(payloadMessage, validationResult);
    } else {
      processPayloadMessage(uftpMessage.sender(), payloadMessage, validationResult);
    }

    return validationResult;
  }

  private void processPayloadMessageResponse(PayloadMessageType response, ValidationResult validationResult) {
    if (!validationResult.valid()) {
      log.warn(String.format("Received invalid %s with MessageID '%s': %s.", response.getClass().getSimpleName(), response.getMessageID(), validationResult.rejectionReason()));
    }
  }

  private void processPayloadMessage(UftpParticipant from, PayloadMessageType payloadMessage, ValidationResult validationResult) {
    var response = UftpValidationResponseCreator.getResponseForMessage(payloadMessage, validationResult);
    var originalRecipient = new UftpParticipant(payloadMessage.getRecipientDomain(), getRecipientRoleBySenderRole(from.role()));

    payloadHandler.notifyNewOutgoingMessage(UftpMessage.createOutgoing(originalRecipient, response));
  }

  private <T extends PayloadMessageType> ValidationResult validateMessage(UftpMessage<T> uftpMessage) {
    if (shouldPerformValidations) {
      return validationService.validate(uftpMessage);
    }
    return ValidationResult.ok();
  }

  public void setShouldPerformValidations(boolean shouldPerformValidations) {
    this.shouldPerformValidations = shouldPerformValidations;
  }
}
