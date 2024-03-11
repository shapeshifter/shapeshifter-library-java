package org.lfenergy.shapeshifter.core.service.receiving;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.*;
import org.lfenergy.shapeshifter.core.model.OutgoingUftpMessage;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

    private final String signedMessageXml = "<SignedMessage/>";

    private final FlexRequest request = new FlexRequest();
    private final String requestXml = "<FlexRequest/>";

    private final FlexRequestResponse response = new FlexRequestResponse();
    private final String responseXml = "<FlexRequestResponse/>";

    private final TestMessage testMessage = new TestMessage();
    private final String testMessageXml = "<TestMessage/>";

    private final TestMessageResponse testMessageResponse = new TestMessageResponse();
    private final String testMessageResponseXml = "<TestMessageResponse/>";

    private final UftpParticipant sender = new UftpParticipant(SENDER_DOMAIN, SENDER_ROLE);

    private final UftpParticipant recipient = new UftpParticipant(RECIPIENT_DOMAIN, RECIPIENT_ROLE);

    @Captor
    private ArgumentCaptor<OutgoingUftpMessage<PayloadMessageType>> outgoingUftpMessageCaptor;

    @Test
    void createAndSendResponseForUftpMessage_valid_request_validationsEnabled() {
        setupRequest();
        testSubject.setShouldPerformValidations(true);

        var incomingUftpMessage = UftpMessage.createIncoming(sender, request, signedMessageXml, requestXml);
        given(validator.validate(incomingUftpMessage)).willReturn(ValidationResult.ok());

        testSubject.process(incomingUftpMessage);

        verify(validator).validate(incomingUftpMessage);
        verify(payloadHandler).notifyNewOutgoingMessage(outgoingUftpMessageCaptor.capture());

        var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
        validateUftpParticipant(outgoingUftpMessage.sender(), recipient);

        var actualResponse = (FlexRequestResponse) outgoingUftpMessage.payloadMessage();

        assertThat(actualResponse.getFlexRequestMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
        assertThat(actualResponse.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
    }

    @Test
    void createAndSendResponseForUftpMessage_invalid_request_validationsEnabled() {
        setupRequest();
        testSubject.setShouldPerformValidations(true);

        var incomingUftpMessage = UftpMessage.createIncoming(sender, request, signedMessageXml, requestXml);
        given(validator.validate(incomingUftpMessage)).willReturn(ValidationResult.rejection(REJECTION_REASON));

        testSubject.process(incomingUftpMessage);

        verify(validator).validate(incomingUftpMessage);
        verify(payloadHandler).notifyNewOutgoingMessage(outgoingUftpMessageCaptor.capture());

        var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
        validateUftpParticipant(outgoingUftpMessage.sender(), recipient);

        var actualResponse = (FlexRequestResponse) outgoingUftpMessage.payloadMessage();

        assertThat(actualResponse.getFlexRequestMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
        assertThat(actualResponse.getResult()).isEqualTo(AcceptedRejectedType.REJECTED);
        assertThat(actualResponse.getRejectionReason()).isEqualTo(REJECTION_REASON);
    }

    @Test
    void createAndSendResponseForUftpMessage_valid_request_validationsDisabled() {
        setupRequest();
        testSubject.setShouldPerformValidations(false);

        testSubject.process(UftpMessage.createIncoming(sender, request, signedMessageXml, requestXml));

        verify(payloadHandler).notifyNewOutgoingMessage(outgoingUftpMessageCaptor.capture());

        var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
        validateUftpParticipant(outgoingUftpMessage.sender(), recipient);

        var actualResponse = (FlexRequestResponse) outgoingUftpMessage.payloadMessage();

        assertThat(actualResponse.getFlexRequestMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
        assertThat(actualResponse.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
    }

    @Test
    void createAndSendResponseForUftpMessage_invalid_request_validationsDisabled() {
        setupRequest();
        testSubject.setShouldPerformValidations(false);

        testSubject.process(UftpMessage.createIncoming(sender, request, signedMessageXml, requestXml));

        verify(payloadHandler).notifyNewOutgoingMessage(outgoingUftpMessageCaptor.capture());

        var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
        validateUftpParticipant(outgoingUftpMessage.sender(), recipient);

        var actualResponse = (FlexRequestResponse) outgoingUftpMessage.payloadMessage();

        assertThat(actualResponse.getFlexRequestMessageID()).isEqualTo(REQUEST_MESSAGE_ID);
        assertThat(actualResponse.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
    }

    @Test
    void createAndSendResponseForUftpMessage_valid_response_validationsEnabled() {
        setupResponse();
        testSubject.setShouldPerformValidations(true);

        var incomingUftpMessage = UftpMessage.createIncoming(sender, response, signedMessageXml, responseXml);
        given(validator.validate(any())).willReturn(ValidationResult.ok());

        testSubject.process(incomingUftpMessage);

        verify(validator).validate(incomingUftpMessage);
        verify(payloadHandler, never()).notifyNewOutgoingMessage(any(OutgoingUftpMessage.class));
    }

    @Test
    void createAndSendResponseForUftpMessage_invalid_response_validationsEnabled() {
        setupResponse();
        testSubject.setShouldPerformValidations(true);

        var incomingUftpMessage = UftpMessage.createIncoming(sender, response, signedMessageXml, responseXml);
        given(validator.validate(incomingUftpMessage)).willReturn(ValidationResult.rejection(REJECTION_REASON));

        testSubject.process(incomingUftpMessage);

        verify(validator).validate(incomingUftpMessage);
        verify(payloadHandler, never()).notifyNewOutgoingMessage(any(OutgoingUftpMessage.class));
    }

    @Test
    void createAndSendResponseForUftpMessage_response_validationsDisabled() {
        testSubject.setShouldPerformValidations(false);

        testSubject.process(UftpMessage.createIncoming(sender, response, signedMessageXml, responseXml));

        verify(validator, never()).validate(any(UftpMessage.class));
        verify(payloadHandler, never()).notifyNewOutgoingMessage(any(OutgoingUftpMessage.class));
    }

    @Test
    void process_valid_testMessage() {
        setupTestMessage();
        testSubject.setShouldPerformValidations(true);

        var incomingUftpMessage = UftpMessage.createIncoming(sender, testMessage, signedMessageXml, testMessageXml);
        given(validator.validate(incomingUftpMessage)).willReturn(ValidationResult.ok());

        var result = testSubject.process(incomingUftpMessage);
        assertThat(result.valid()).isTrue();

        verify(payloadHandler).notifyNewOutgoingMessage(outgoingUftpMessageCaptor.capture());

        var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
        var participant = outgoingUftpMessage.sender();
        assertThat(participant.domain()).isEqualTo(RECIPIENT_DOMAIN);
        assertThat(participant.role()).isEqualTo(USEFRoleType.AGR);

        var response = (TestMessageResponse) outgoingUftpMessage.payloadMessage();
        assertThat(response.getSenderDomain()).isEqualTo(RECIPIENT_DOMAIN);
        assertThat(response.getConversationID()).isEqualTo(CONVERSATION_ID);
    }

    @Test
    void process_invalid_testMessage() {
        setupTestMessage();
        testSubject.setShouldPerformValidations(true);

        var incomingUftpMessage = UftpMessage.createIncoming(sender, testMessage, signedMessageXml, testMessageXml);
        given(validator.validate(incomingUftpMessage)).willReturn(ValidationResult.rejection(REJECTION_REASON));

        var result = testSubject.process(incomingUftpMessage);
        assertThat(result.valid()).isFalse();

        verify(payloadHandler).notifyNewOutgoingMessage(outgoingUftpMessageCaptor.capture());

        var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
        var participant = outgoingUftpMessage.sender();
        assertThat(participant.domain()).isEqualTo(RECIPIENT_DOMAIN);
        assertThat(participant.role()).isEqualTo(USEFRoleType.AGR);

        var response = (TestMessageResponse) outgoingUftpMessage.payloadMessage();
        assertThat(response.getSenderDomain()).isEqualTo(RECIPIENT_DOMAIN);
        assertThat(response.getConversationID()).isEqualTo(CONVERSATION_ID);
    }

    @Test
    void process_testMessageResponse() {
        testSubject.setShouldPerformValidations(true);

        var incomingUftpMessage = UftpMessage.createIncoming(sender, testMessageResponse, signedMessageXml, testMessageResponseXml);
        given(validator.validate(incomingUftpMessage)).willReturn(ValidationResult.ok());

        var result = testSubject.process(incomingUftpMessage);
        assertThat(result.valid()).isTrue();

        verify(payloadHandler, never()).notifyNewOutgoingMessage(any(OutgoingUftpMessage.class));
    }

    @Test
    void process_invalid_testMessageResponse() {
        testSubject.setShouldPerformValidations(true);

        var incomingUftpMessage = UftpMessage.createIncoming(sender, testMessageResponse, signedMessageXml, testMessageResponseXml);
        given(validator.validate(incomingUftpMessage)).willReturn(ValidationResult.rejection("test reason"));

        var result = testSubject.process(incomingUftpMessage);
        assertThat(result.valid()).isFalse();

        verify(payloadHandler, never()).notifyNewOutgoingMessage(any(OutgoingUftpMessage.class));
    }

    private void setupRequest() {
        request.setMessageID(REQUEST_MESSAGE_ID);
        request.setSenderDomain(SENDER_DOMAIN);
        request.setRecipientDomain(RECIPIENT_DOMAIN);
    }

    private void setupResponse() {
        response.setMessageID(RESPONSE_MESSAGE_ID);
    }

    private void validateUftpParticipant(UftpParticipant actual, UftpParticipant expected) {
        assertThat(actual.domain()).isEqualTo(expected.domain());
        assertThat(actual.role()).isEqualTo(expected.role());
    }

    private void setupTestMessage() {
        testMessage.setConversationID(CONVERSATION_ID);
        testMessage.setRecipientDomain(RECIPIENT_DOMAIN);
    }

}