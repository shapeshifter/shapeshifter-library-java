// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.receiving;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.AcceptedRejectedType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.core.service.receiving.response.UftpValidationResponseCreator;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpValidationResponseCreatorTest {

  private static final String REFERENCE_MESSAGE_ID = "referenceMessageId";
  private static final String CONVERSATION_ID = "conversationId";
  private static final String RECIPIENT_DOMAIN = "recipient.domain";
  private static final String SENDER_DOMAIN = "sender.domain";
  private static final String REJECTION_REASON = "rejectionReason";
  private static final String VERSION = "3.1.0";

  @Test
  void getResponseForMessage_valid() {
    var request = createTestFlexRequest();
    var validationResult = new ValidationResult(true, null);

    var response = (FlexRequestResponse) UftpValidationResponseCreator.getResponseForMessage(request, validationResult);

    validateFlexRequestResponse(response);

    assertThat(response.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
    assertThat(response.getRejectionReason()).isNull();
  }

  @Test
  void getResponseForMessage_invalid() {
    var request = createTestFlexRequest();
    var validationResult = new ValidationResult(false, REJECTION_REASON);

    var response = (FlexRequestResponse) UftpValidationResponseCreator.getResponseForMessage(request, validationResult);

    validateFlexRequestResponse(response);

    assertThat(response.getResult()).isEqualTo(AcceptedRejectedType.REJECTED);
    assertThat(response.getRejectionReason()).isEqualTo(REJECTION_REASON);
  }

  @Test
  void getResponseForTestMessage() {
    var request = createTestMessage();

    var response = UftpValidationResponseCreator.getResponseForMessage(request, ValidationResult.ok());

    assertThat(response.getRecipientDomain()).isEqualTo(SENDER_DOMAIN);
    assertThat(response.getSenderDomain()).isEqualTo(RECIPIENT_DOMAIN);
    assertThat(response.getConversationID()).isEqualTo(CONVERSATION_ID);
    assertThat(response.getVersion()).isEqualTo(VERSION);
  }

  private void validateFlexRequestResponse(FlexRequestResponse response) {
    assertThat(response.getFlexRequestMessageID()).isEqualTo(REFERENCE_MESSAGE_ID);
    assertThat(response.getRecipientDomain()).isEqualTo(SENDER_DOMAIN);
    assertThat(response.getSenderDomain()).isEqualTo(RECIPIENT_DOMAIN);
    assertThat(response.getConversationID()).isEqualTo(CONVERSATION_ID);
    assertThat(response.getVersion()).isEqualTo(VERSION);
  }

  private FlexRequest createTestFlexRequest() {
    var request = new FlexRequest();
    request.setRecipientDomain(RECIPIENT_DOMAIN);
    request.setSenderDomain(SENDER_DOMAIN);
    request.setMessageID(REFERENCE_MESSAGE_ID);
    request.setConversationID(CONVERSATION_ID);
    request.setVersion(VERSION);
    return request;
  }

  private TestMessage createTestMessage() {
    var testMessage = new TestMessage();
    testMessage.setRecipientDomain(RECIPIENT_DOMAIN);
    testMessage.setSenderDomain(SENDER_DOMAIN);
    testMessage.setMessageID(REFERENCE_MESSAGE_ID);
    testMessage.setConversationID(CONVERSATION_ID);
    testMessage.setVersion(VERSION);
    return testMessage;
  }

}
