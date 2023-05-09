package org.lfenergy.shapeshifter.core.service.receiving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.AcceptedRejectedType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.handler.UftpPayloadHandler;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpReceivedMessageServiceTest {

  private static final String REQUEST_MESSAGE_ID = UUID.randomUUID().toString();
  private static final String RESPONSE_MESSAGE_ID = UUID.randomUUID().toString();
  private static final String SENDER_DOMAIN = "SENDER_DOMAIN";
  private static final USEFRoleType SENDER_ROLE = USEFRoleType.DSO;
  private static final String RECIPIENT_DOMAIN = "RECIPIENT_DOMAIN";
  private static final USEFRoleType RECIPIENT_ROLE = USEFRoleType.AGR;
  private static final String REJECTION_REASON = "Reason for rejection";
  private static final String CONVERSATION_ID = UUID.randomUUID().toString();

  @Mock
  private UftpValidationService validator;

  @Mock
  private UftpPayloadHandler payloadHandler;

  @InjectMocks
  private UftpReceivedMessageService testSubject;

  @Mock
  private FlexRequest request;

  @Mock
  private FlexRequestResponse response;

  @Mock
  private TestMessage testMessage;

  @Mock
  private TestMessageResponse testMessageResponse;

  private UftpParticipant sender;

  private UftpParticipant recipient;

  @Captor
  private ArgumentCaptor<UftpParticipant> recipientCaptor;

  @Captor
  private ArgumentCaptor<UftpParticipant> senderCaptor;

  @Captor
  private ArgumentCaptor<UftpMessage<PayloadMessageType>> uftpMessageCaptor;

  @Captor
  private ArgumentCaptor<PayloadMessageType> responseCaptor;

  @Captor
  private ArgumentCaptor<TestMessageResponse> testMessageResponseCaptor;

  @BeforeEach
  void setUp() {
    this.sender = new UftpParticipant(SENDER_DOMAIN, SENDER_ROLE);
    this.recipient = new UftpParticipant(RECIPIENT_DOMAIN, RECIPIENT_ROLE);
  }

  @Test
  void createAndSendResponseForUftpMessage_valid_request_validationsEnabled() {
    setupRequest();
    testSubject.setShouldPerformValidations(true);

    given(validator.validate(uftpMessageCaptor.capture())).willReturn(ValidationResult.ok());

    testSubject.process(sender, request);

    verify(payloadHandler).notifyNewOutgoingMessage(recipientCaptor.capture(), responseCaptor.capture());

    assertThat(uftpMessageCaptor.getAllValues().get(0).sender()).isNotNull();
    var actualSender = uftpMessageCaptor.getAllValues().get(0).sender();
    validateUftpParticipant(actualSender, sender);

    assertThat(recipientCaptor.getAllValues()).hasSize(1);
    var actualRecipient = recipientCaptor.getValue();
    validateUftpParticipant(actualRecipient, recipient);

    var uftpMessages = uftpMessageCaptor.getAllValues();
    assertThat(uftpMessages).hasSize(1);
    var actualRequest = uftpMessages.get(0).payloadMessage();

    assertThat(responseCaptor.getAllValues()).hasSize(1);
    var actualResponse = (FlexRequestResponse) responseCaptor.getValue();

    assertThat(actualRequest.getMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
    assertThat(actualResponse.getFlexRequestMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
    assertThat(actualResponse.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
  }

  @Test
  void createAndSendResponseForUftpMessage_invalid_request_validationsEnabled() {
    setupRequest();
    testSubject.setShouldPerformValidations(true);

    given(validator.validate(uftpMessageCaptor.capture())).willReturn(ValidationResult.rejection(REJECTION_REASON));

    testSubject.process(sender, request);

    verify(payloadHandler).notifyNewOutgoingMessage(recipientCaptor.capture(), responseCaptor.capture());

    var uftpMessages = uftpMessageCaptor.getAllValues();
    assertThat(uftpMessages.get(0).sender()).isNotNull();
    var actualSender = uftpMessages.get(0).sender();
    validateUftpParticipant(actualSender, sender);

    assertThat(recipientCaptor.getAllValues()).hasSize(1);
    var actualRecipient = recipientCaptor.getValue();
    validateUftpParticipant(actualRecipient, recipient);

    var actualRequest = uftpMessages.get(0).payloadMessage();

    assertThat(responseCaptor.getAllValues()).hasSize(1);
    var actualResponse = (FlexRequestResponse) responseCaptor.getValue();

    assertThat(actualRequest.getMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
    assertThat(actualResponse.getFlexRequestMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
    assertThat(actualResponse.getResult()).isEqualTo(AcceptedRejectedType.REJECTED);
    assertThat(actualResponse.getRejectionReason()).isEqualTo(REJECTION_REASON);
  }

  @Test
  void createAndSendResponseForUftpMessage_valid_request_validationsDisabled() {
    setupRequest();
    testSubject.setShouldPerformValidations(false);

    testSubject.process(sender, request);

    verify(payloadHandler).notifyNewOutgoingMessage(recipientCaptor.capture(), responseCaptor.capture());

    assertThat(recipientCaptor.getAllValues()).hasSize(1);
    var actualRecipient = recipientCaptor.getValue();
    validateUftpParticipant(actualRecipient, recipient);

    assertThat(responseCaptor.getAllValues()).hasSize(1);
    var actualResponse = (FlexRequestResponse) responseCaptor.getValue();

    assertThat(actualResponse.getFlexRequestMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
    assertThat(actualResponse.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
  }

  @Test
  void createAndSendResponseForUftpMessage_invalid_request_validationsDisabled() {
    setupRequest();
    testSubject.setShouldPerformValidations(false);

    testSubject.process(sender, request);

    verify(payloadHandler).notifyNewOutgoingMessage(recipientCaptor.capture(), responseCaptor.capture());

    assertThat(recipientCaptor.getAllValues()).hasSize(1);
    var actualRecipient = recipientCaptor.getValue();
    validateUftpParticipant(actualRecipient, recipient);

    assertThat(responseCaptor.getAllValues()).hasSize(1);
    var actualResponse = (FlexRequestResponse) responseCaptor.getValue();

    assertThat(actualResponse.getFlexRequestMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
    assertThat(actualResponse.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
  }

  @Test
  void createAndSendResponseForUftpMessage_valid_response_validationsEnabled() {
    setupResponse();
    testSubject.setShouldPerformValidations(true);

    given(validator.validate(uftpMessageCaptor.capture())).willReturn(ValidationResult.ok());

    testSubject.process(recipient, response);

    var uftpMessages = uftpMessageCaptor.getAllValues();
    var actualSender = uftpMessages.get(0).sender();
    assertThat(actualSender).isNotNull();
    validateUftpParticipant(actualSender, recipient);
    var actualRequest = uftpMessages.get(0).payloadMessage();

    assertThat(actualRequest.getMessageID()).isEqualTo(RESPONSE_MESSAGE_ID);
  }

  @Test
  void createAndSendResponseForUftpMessage_invalid_response_validationsEnabled() {
    setupResponse();
    testSubject.setShouldPerformValidations(true);

    given(validator.validate(uftpMessageCaptor.capture())).willReturn(ValidationResult.rejection(REJECTION_REASON));

    testSubject.process(recipient, response);

    assertThat(uftpMessageCaptor.getAllValues().get(0).sender()).isNotNull();
    var actualSender = uftpMessageCaptor.getAllValues().get(0).sender();
    validateUftpParticipant(actualSender, recipient);

    assertThat(uftpMessageCaptor.getAllValues().get(0).payloadMessage()).isNotNull();
    var actualRequest = uftpMessageCaptor.getAllValues().get(0).payloadMessage();

    assertThat(actualRequest.getMessageID()).isEqualTo(RESPONSE_MESSAGE_ID);
  }

  @Test
  void createAndSendResponseForUftpMessage_response_validationsDisabled() {
    testSubject.setShouldPerformValidations(false);

    testSubject.process(recipient, response);

    verify(validator, never()).validate(any(UftpMessage.class));
    verify(payloadHandler, never()).notifyNewOutgoingMessage(any(UftpParticipant.class), any(PayloadMessageType.class));
  }

  @Test
  void process_valid_testMessage() {
    setupTestMessage();
    testSubject.setShouldPerformValidations(true);

    given(validator.validate(UftpMessage.createIncoming(sender, testMessage))).willReturn(ValidationResult.ok());

    var result = testSubject.process(sender, testMessage);
    assertThat(result.valid()).isTrue();

    verify(payloadHandler).notifyNewOutgoingMessage(senderCaptor.capture(), testMessageResponseCaptor.capture());

    var participant = senderCaptor.getValue();
    assertThat(participant.domain()).isEqualTo(RECIPIENT_DOMAIN);
    assertThat(participant.role()).isEqualTo(USEFRoleType.AGR);

    var response = testMessageResponseCaptor.getValue();
    assertThat(response.getSenderDomain()).isEqualTo(RECIPIENT_DOMAIN);
    assertThat(response.getConversationID()).isEqualTo(CONVERSATION_ID);
  }

  @Test
  void process_invalid_testMessage() {
    setupTestMessage();
    testSubject.setShouldPerformValidations(true);

    given(validator.validate(UftpMessage.createIncoming(sender, testMessage))).willReturn(ValidationResult.rejection(REJECTION_REASON));

    var result = testSubject.process(sender, testMessage);
    assertThat(result.valid()).isFalse();

    verify(payloadHandler).notifyNewOutgoingMessage(senderCaptor.capture(), testMessageResponseCaptor.capture());

    var participant = senderCaptor.getValue();
    assertThat(participant.domain()).isEqualTo(RECIPIENT_DOMAIN);
    assertThat(participant.role()).isEqualTo(USEFRoleType.AGR);

    var response = testMessageResponseCaptor.getValue();
    assertThat(response.getSenderDomain()).isEqualTo(RECIPIENT_DOMAIN);
    assertThat(response.getConversationID()).isEqualTo(CONVERSATION_ID);
  }

  @Test
  void process_testMessageResponse() {
    testSubject.setShouldPerformValidations(true);

    given(validator.validate(UftpMessage.createIncoming(sender, testMessageResponse))).willReturn(ValidationResult.ok());

    var result = testSubject.process(sender, testMessageResponse);
    assertThat(result.valid()).isTrue();

    verify(payloadHandler, never()).notifyNewOutgoingMessage(any(), any());
  }

  @Test
  void process_invalid_testMessageResponse() {
    testSubject.setShouldPerformValidations(true);

    given(validator.validate(UftpMessage.createIncoming(sender, testMessageResponse))).willReturn(ValidationResult.rejection("test reason"));

    var result = testSubject.process(sender, testMessageResponse);
    assertThat(result.valid()).isFalse();

    verify(payloadHandler, never()).notifyNewOutgoingMessage(any(), any());
  }

  private void setupRequest() {
    given(request.getMessageID()).willReturn(REQUEST_MESSAGE_ID);
    given(request.getSenderDomain()).willReturn(SENDER_DOMAIN);
    given(request.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);
  }

  private void setupResponse() {
    given(response.getMessageID()).willReturn(RESPONSE_MESSAGE_ID);
  }

  private void validateUftpParticipant(UftpParticipant actual, UftpParticipant expected) {
    assertThat(actual.domain()).isEqualTo(expected.domain());
    assertThat(actual.role()).isEqualTo(expected.role());
  }

  private void setupTestMessage() {
    given(testMessage.getConversationID()).willReturn(CONVERSATION_ID);
    given(testMessage.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);
  }

}