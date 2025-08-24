// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.service.receiving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.core.common.xsd.XsdValidationException;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.crypto.UftpVerifyException;
import org.lfenergy.shapeshifter.core.service.receiving.ReceivedMessageProcessor;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UftpInternalControllerTest {

  private static final String TRANSPORT_XML = "TRANSPORT_XML";
  private static final String PAYLOAD_XML = "PAYLOAD_XML";
  private static final String SENDER_DOMAIN = "SENDER_DOMAIN";
  private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

  @Mock
  private UftpSerializer deserializer;
  @Mock
  private UftpCryptoService uftpCryptoService;
  @Mock
  private ReceivedMessageProcessor processor;
  @Mock
  private UftpErrorProcessor errorProcessor;

  @InjectMocks
  private UftpInternalController testSubject;

  @Mock
  private SignedMessage signedMessage;
  @Mock
  private PayloadMessageType payloadMessage;
  @Captor
  private ArgumentCaptor<UftpConnectorException> uftpExceptionCaptor;
  @Captor
  private ArgumentCaptor<IncomingUftpMessage<? extends PayloadMessageType>> incomingUftpMessageCaptor;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        deserializer,
        uftpCryptoService,
        processor,
        errorProcessor,
        signedMessage,
        payloadMessage
    );
  }

  @Test
  void receiveUftpMessageXsdValidationException() {
    var uftpException = new XsdValidationException(ERROR_MESSAGE);
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willThrow(uftpException);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCode().value()).isEqualTo(400);
    assertThat(result.getBody()).isEqualTo("Failed to process received UFTP message. Error: ERROR_MESSAGE");
    verify(errorProcessor).onErrorDuringReceivedMessageReading(TRANSPORT_XML, uftpException);
  }

  @Test
  void receiveUftpMessageUftpVerifyException() {
    var uftpException = new UftpVerifyException(ERROR_MESSAGE);
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willThrow(uftpException);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCode().value()).isEqualTo(401);
    assertThat(result.getBody()).isEqualTo("Failed to process received UFTP message. Error: ERROR_MESSAGE");
    verify(errorProcessor).onErrorDuringReceivedMessageReading(TRANSPORT_XML, uftpException);
  }

  @Test
  void receiveUftpMessageUftpNotImplementedException() {
    var uftpException = new UftpVerifyException(ERROR_MESSAGE);
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willThrow(uftpException);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCode().value()).isEqualTo(501);
    assertThat(result.getBody()).isEqualTo("Failed to process received UFTP message. Error: ERROR_MESSAGE");
    verify(errorProcessor).onErrorDuringReceivedMessageReading(TRANSPORT_XML, uftpException);
  }

  @Test
  void receiveUftpMessageUftpConnectorException() {
    var uftpException = new UftpConnectorException(ERROR_MESSAGE);
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willThrow(uftpException);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCode().value()).isEqualTo(500);
    assertThat(result.getBody()).isEqualTo("Failed to process received UFTP message. Error: ERROR_MESSAGE");
    verify(errorProcessor).onErrorDuringReceivedMessageReading(TRANSPORT_XML, uftpException);
  }

  @Test
  void receiveUftpMessageOtherException() {
    var exception = new RuntimeException(ERROR_MESSAGE);
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willThrow(exception);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCode().value()).isEqualTo(500);
    assertThat(result.getBody()).isEqualTo("Failed to process received UFTP message. Error: ERROR_MESSAGE");
    verify(errorProcessor).onErrorDuringReceivedMessageReading(eq(TRANSPORT_XML), uftpExceptionCaptor.capture());

    assertThat(uftpExceptionCaptor.getAllValues()).hasSize(1);
    assertThat(uftpExceptionCaptor.getValue()).isSameAs(exception);
  }

  @Test
  void receiveUftpMessageOk() {
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willReturn(signedMessage);
    given(signedMessage.getSenderDomain()).willReturn(SENDER_DOMAIN);
    given(signedMessage.getSenderRole()).willReturn(USEFRoleType.DSO);
    given(uftpCryptoService.verifySignedMessage(signedMessage)).willReturn(PAYLOAD_XML);
    given(deserializer.fromPayloadXml(PAYLOAD_XML)).willReturn(payloadMessage);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNull();
    verify(processor).onReceivedMessage(incomingUftpMessageCaptor.capture());

    var incomingUftpMessage = incomingUftpMessageCaptor.getValue();
    assertThat(incomingUftpMessage.sender().domain()).isEqualTo(SENDER_DOMAIN);
    assertThat(incomingUftpMessage.sender().role()).isEqualTo(USEFRoleType.DSO);
    assertThat(incomingUftpMessage.payloadMessage()).isSameAs(payloadMessage);
    assertThat(incomingUftpMessage.signedMessageXml()).isEqualTo(TRANSPORT_XML);
    assertThat(incomingUftpMessage.payloadMessageXml()).isEqualTo(PAYLOAD_XML);
  }
}