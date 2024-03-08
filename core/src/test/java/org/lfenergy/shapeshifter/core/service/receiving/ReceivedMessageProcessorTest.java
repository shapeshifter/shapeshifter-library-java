// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.receiving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lfenergy.shapeshifter.core.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.DUPLICATE_MESSAGE;
import static org.lfenergy.shapeshifter.core.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.NEW_MESSAGE;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.core.service.handler.UftpPayloadHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReceivedMessageProcessorTest {

  private static final String SENDER_DOMAIN = "SENDER_DOMAIN";
  private static final USEFRoleType SENDER_ROLE = USEFRoleType.AGR;
  private static final String MESSAGE_ID = "MESSAGE_ID";
  private static final String SIGNED_MESSAGE_XML = "TRANSPORT_XML";
  private static final String PAYLOAD_MESSAGE_XML = "PAYLOAD_XML";

  @Mock
  private UftpPayloadHandler payloadHandler;
  @Mock
  private DuplicateMessageDetection duplicateDetection;
  @Mock
  private UftpErrorProcessor errorProcessor;

  @InjectMocks
  private ReceivedMessageProcessor testSubject;

  @Mock
  private SignedMessage signedMessage;
  @Mock
  private FlexRequest businessMsg;
  @Mock
  private FlexRequestResponse responseMsg;
  @Captor
  private ArgumentCaptor<UftpParticipant> senderCaptor;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        payloadHandler,
        duplicateDetection,
        signedMessage,
        businessMsg,
        responseMsg
    );
  }

  void noMoreErrorInteractionProcessor() {
    verifyNoMoreInteractions(
        errorProcessor
    );
  }

  private void mockSenderSignedMessage() {
    given(signedMessage.getSenderDomain()).willReturn(SENDER_DOMAIN);
    given(signedMessage.getSenderRole()).willReturn(SENDER_ROLE);
  }

  @Test
  void onReceivedMessage_businessMsg_smoothSailing() {
    mockSenderSignedMessage();

    given(duplicateDetection.isDuplicate(businessMsg)).willReturn(NEW_MESSAGE);

    var incomingUftpMessage = IncomingUftpMessage.create(new UftpParticipant(signedMessage), businessMsg, SIGNED_MESSAGE_XML, PAYLOAD_MESSAGE_XML);
    testSubject.onReceivedMessage(incomingUftpMessage);

    verify(payloadHandler, times(1)).notifyNewIncomingMessage(incomingUftpMessage);

    noMoreErrorInteractionProcessor();
  }

  @Test
  void onReceivedMessage_businessMsg_exceptionWhileProcessing() {
    mockSenderSignedMessage();

    var exception = new RuntimeException("Simulated error during duplicate detection");
    given(duplicateDetection.isDuplicate(businessMsg)).willThrow(exception);

    var incomingUftpMessage = IncomingUftpMessage.create(new UftpParticipant(signedMessage), businessMsg, SIGNED_MESSAGE_XML, PAYLOAD_MESSAGE_XML);
    assertThatThrownBy(() -> testSubject.onReceivedMessage(incomingUftpMessage))
        .isSameAs(exception);
  }

  @Test
  void onReceivedMessage_businessMsg_isDuplicate() {
    mockSenderSignedMessage();
    given(duplicateDetection.isDuplicate(businessMsg)).willReturn(DUPLICATE_MESSAGE);
    given(businessMsg.getMessageID()).willReturn(MESSAGE_ID);

    var incomingUftpMessage = IncomingUftpMessage.create(new UftpParticipant(signedMessage), businessMsg, SIGNED_MESSAGE_XML, PAYLOAD_MESSAGE_XML);
    assertThatThrownBy(() -> testSubject.onReceivedMessage(incomingUftpMessage))
        .isInstanceOf(DuplicateMessageException.class);

    verify(errorProcessor).onDuplicateReceived(senderCaptor.capture(), eq(businessMsg));
    assertThat(senderCaptor.getAllValues()).hasSize(1);
    UftpParticipant sender = senderCaptor.getValue();
    assertThat(sender.domain()).isEqualTo(SENDER_DOMAIN);
    assertThat(sender.role()).isEqualTo(SENDER_ROLE);
  }

  @Test
  void onReceivedMessage_responseMsg_smoothSailing() {
    mockSenderSignedMessage();

    given(duplicateDetection.isDuplicate(responseMsg)).willReturn(NEW_MESSAGE);

    var incomingUftpMessage = IncomingUftpMessage.create(new UftpParticipant(signedMessage), responseMsg, SIGNED_MESSAGE_XML, PAYLOAD_MESSAGE_XML);
    testSubject.onReceivedMessage(incomingUftpMessage);

    verify(payloadHandler, times(1)).notifyNewIncomingMessage(incomingUftpMessage);

    noMoreErrorInteractionProcessor();
  }

}