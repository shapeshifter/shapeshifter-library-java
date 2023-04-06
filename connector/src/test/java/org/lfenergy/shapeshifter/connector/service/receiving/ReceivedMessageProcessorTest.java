// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.receiving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.DUPLICATE_MESSAGE;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.NEW_MESSAGE;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.concurrent.ForkJoinPool;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.connector.service.handler.UftpPayloadHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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

    int startThreadCount = numberOfThreads();
    testSubject.onReceivedMessage(signedMessage, businessMsg);
    waitFinished(startThreadCount);

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
    given(duplicateDetection.isDuplicate(businessMsg)).willThrow(new RuntimeException("This message has already been submitted"));

    int startThreadCount = numberOfThreads();
    assertThatThrownBy(() -> testSubject.onReceivedMessage(signedMessage, businessMsg))
        .isInstanceOf(UftpConnectorException.class)
        .hasMessage("Exception during processing of FlexRequest; could not determine whether this message was already submitted");
    waitFinished(startThreadCount);
  }

  @Test
  void onReceivedMessage_businessMsg_isDuplicate() {
    mockSenderSignedMessage();
    given(duplicateDetection.isDuplicate(businessMsg)).willReturn(DUPLICATE_MESSAGE);
    given(businessMsg.getMessageID()).willReturn(MESSAGE_ID);

    int startThreadCount = numberOfThreads();

    var thrown = assertThrows(UftpConnectorException.class,
                              () -> testSubject.onReceivedMessage(signedMessage, businessMsg));

    assertThat(thrown.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    waitFinished(startThreadCount);

    verify(errorProcessor).duplicateReceived(senderCaptor.capture(), eq(businessMsg));
    assertThat(senderCaptor.getAllValues()).hasSize(1);
    UftpParticipant sender = senderCaptor.getValue();
    assertThat(sender.domain()).isEqualTo(SENDER_DOMAIN);
    assertThat(sender.role()).isEqualTo(SENDER_ROLE);
  }

  @Test
  void onReceivedMessage_responseMsg_smoothSailing() {
    mockSenderSignedMessage();

    given(duplicateDetection.isDuplicate(responseMsg)).willReturn(NEW_MESSAGE);

    int startThreadCount = numberOfThreads();
    testSubject.onReceivedMessage(signedMessage, responseMsg);
    waitFinished(startThreadCount);

    verify(payloadHandler, times(1)).notifyNewIncomingMessage(senderCaptor.capture(), eq(responseMsg));

    assertThat(senderCaptor.getAllValues()).hasSize(1);
    UftpParticipant sender = senderCaptor.getValue();
    assertThat(sender.domain()).isEqualTo(SENDER_DOMAIN);
    assertThat(sender.role()).isEqualTo(SENDER_ROLE);

    noMoreErrorInteractionProcessor();
  }

  @Test
  void onReceivedMessage_responseMsg_isDuplicate() {
    mockSenderSignedMessage();
    given(duplicateDetection.isDuplicate(responseMsg)).willReturn(DUPLICATE_MESSAGE);
    given(responseMsg.getMessageID()).willReturn(MESSAGE_ID);

    var thrown = assertThrows(UftpConnectorException.class,
                              () -> testSubject.onReceivedMessage(signedMessage, responseMsg));

    assertThat(thrown.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    verify(errorProcessor).duplicateReceived(senderCaptor.capture(), eq(responseMsg));
    assertThat(senderCaptor.getAllValues()).hasSize(1);
    UftpParticipant sender = senderCaptor.getValue();
    assertThat(sender.domain()).isEqualTo(SENDER_DOMAIN);
    assertThat(sender.role()).isEqualTo(SENDER_ROLE);
  }

  @SneakyThrows
  private void waitFinished(int startThreadCount) {
    System.out.println("startThreadCount: " + startThreadCount);
    int loops = 0;
    int currentThreadCount = numberOfThreads();
    do {
      currentThreadCount = numberOfThreads();
      System.out.println("currentThreadCount: " + currentThreadCount);
      Thread.sleep(10);
      loops++;
    } while (currentThreadCount > startThreadCount && loops < 100);
  }

  private int numberOfThreads() {
    return ForkJoinPool.commonPool().getActiveThreadCount();
  }
}