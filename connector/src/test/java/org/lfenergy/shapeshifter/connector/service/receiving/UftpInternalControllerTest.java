package org.lfenergy.shapeshifter.connector.service.receiving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;
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
  @Mock
  private UftpConnectorException uftpException;
  @Mock
  private RuntimeException runtimeException;
  @Captor
  private ArgumentCaptor<UftpConnectorException> uftpExceptionCaptor;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        deserializer,
        uftpCryptoService,
        processor,
        errorProcessor,
        signedMessage,
        payloadMessage,
        uftpException,
        runtimeException
    );
  }

  @Test
  void receiveUftpMessageUftpConnectorException() {
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willThrow(uftpException);
    given(uftpException.getMessage()).willReturn(ERROR_MESSAGE);
    given(uftpException.getHttpStatusCode()).willReturn(456);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCodeValue()).isEqualTo(456);
    assertThat(result.getBody()).isEqualTo("Failed to process received UFTP message. Error: ERROR_MESSAGE");
    verify(errorProcessor).onErrorDuringReceivedMessageReading(TRANSPORT_XML, uftpException);
  }

  @Test
  void receiveUftpMessageOtherException() {
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willThrow(runtimeException);
    given(runtimeException.getMessage()).willReturn(ERROR_MESSAGE);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCodeValue()).isEqualTo(500);
    assertThat(result.getBody()).isEqualTo("Failed to process received UFTP message. Error: ERROR_MESSAGE");
    verify(errorProcessor).onErrorDuringReceivedMessageReading(eq(TRANSPORT_XML), uftpExceptionCaptor.capture());

    assertThat(uftpExceptionCaptor.getAllValues()).hasSize(1);
    assertException(uftpExceptionCaptor.getValue(), ERROR_MESSAGE, runtimeException, 500);
  }

  @Test
  void receiveUftpMessageOk() {
    given(deserializer.fromSignedXml(TRANSPORT_XML)).willReturn(signedMessage);
    given(signedMessage.getSenderDomain()).willReturn(SENDER_DOMAIN);
    given(uftpCryptoService.unsealMessage(signedMessage)).willReturn(PAYLOAD_XML);
    given(deserializer.fromPayloadXml(PAYLOAD_XML)).willReturn(payloadMessage);

    var result = testSubject.postUftpMessage(TRANSPORT_XML);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNull();
    verify(processor).onReceivedMessage(signedMessage, payloadMessage);
  }
}