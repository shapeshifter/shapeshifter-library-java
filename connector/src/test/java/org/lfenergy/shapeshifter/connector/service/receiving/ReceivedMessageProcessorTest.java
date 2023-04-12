// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.receiving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.DUPLICATE_MESSAGE;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.NEW_MESSAGE;
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
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.connector.service.handler.UftpPayloadHandler;
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

    testSubject.onReceivedMessage(signedMessage, businessMsg);

    verify(payloadHandler, times(1)).notifyNewIncomingMessage(senderCaptor.capture(), eq(businessMsg));

    assertThat(senderCaptor.getAllValues()).hasSize(1);
    UftpParticipant sender = senderCaptor.getValue();
    assertThat(sender.domain()).isEqualTo(SENDER_DOMAIN);
    assertThat(sender.role()).isEqualTo(SENDER_ROLE);

    noMoreErrorInteractionProcessor();
  }

  @Test
  void onReceivedMessage_businessMsg_exceptionWhileProcessing() {
    mockSenderSignedMessage();

    var exception = new RuntimeException("Simulated error during duplicate detection");
    given(duplicateDetection.isDuplicate(businessMsg)).willThrow(exception);

    assertThatThrownBy(() -> testSubject.onReceivedMessage(signedMessage, businessMsg))
        .isSameAs(exception);
  }

  @Test
  void onReceivedMessage_businessMsg_isDuplicate() {
    mockSenderSignedMessage();
    given(duplicateDetection.isDuplicate(businessMsg)).willReturn(DUPLICATE_MESSAGE);
    given(businessMsg.getMessageID()).willReturn(MESSAGE_ID);

    assertThatThrownBy(() -> testSubject.onReceivedMessage(signedMessage, businessMsg))
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

    testSubject.onReceivedMessage(signedMessage, responseMsg);

    verify(payloadHandler, times(1)).notifyNewIncomingMessage(senderCaptor.capture(), eq(responseMsg));

    assertThat(senderCaptor.getAllValues()).hasSize(1);
    UftpParticipant sender = senderCaptor.getValue();
    assertThat(sender.domain()).isEqualTo(SENDER_DOMAIN);
    assertThat(sender.role()).isEqualTo(SENDER_ROLE);

    noMoreErrorInteractionProcessor();
  }

}