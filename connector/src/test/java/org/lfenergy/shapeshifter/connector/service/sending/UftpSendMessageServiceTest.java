package org.lfenergy.shapeshifter.connector.service.sending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertException;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertExceptionCauseInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.ShippingDetails;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class UftpSendMessageServiceTest {

  private static final String PAYLOAD_XML = "PAYLOAD_XML";
  public static final String PRIVATE_KEY = "PRIVATE_KEY";
  private static final String SIGNED_XML = "SIGNED_XML";
  private static final String ENDPOINT = "ENDPOINT";
  private static final String BODY_ERROR_MESSAGE = "BODY_ERROR_MESSAGE";

  @Mock
  private UftpSerializer serializer;
  @Mock
  private UftpCryptoService cryptoService;
  @Mock
  private ParticipantResolutionService participantService;
  @Mock
  private UftpSendFactory factory;

  @InjectMocks
  private UftpSendMessageService testSubject;

  @Mock
  private FlexRequest flexRequest;
  @Mock
  private ShippingDetails details;
  @Mock
  private UftpParticipant sender;
  @Mock
  private SignedMessage signedMessage;
  @Mock
  private UftpParticipant recipient;
  @Mock
  private RestTemplate template;
  @Mock
  private ResponseEntity<String> response;

  @Captor
  private ArgumentCaptor<HttpEntity<String>> requestCaptor;

  @AfterEach
  public void noMore() {
    verifyNoMoreInteractions(
        serializer,
        cryptoService,
        participantService,
        factory,
        flexRequest,
        details,
        sender,
        signedMessage,
        recipient,
        template,
        response
    );
  }

  @Test
  void attemptToSendMessage_ok() {
    mockSerialisation();
    mockSending();
    given(response.getStatusCode()).willReturn(HttpStatus.OK);

    testSubject.attemptToSendMessage(flexRequest, details);

    verifySending();
  }

  private void mockSerialisation() {
    given(serializer.toXml(flexRequest)).willReturn(PAYLOAD_XML);
    given(details.sender()).willReturn(sender);
    given(details.senderPrivateKey()).willReturn(PRIVATE_KEY);
    given(cryptoService.sealMessage(PAYLOAD_XML, sender, PRIVATE_KEY)).willReturn(signedMessage);
    given(serializer.toXml(signedMessage)).willReturn(SIGNED_XML);
  }

  private void mockSending() {
    given(details.recipient()).willReturn(recipient);
    given(participantService.getEndPointUrl(recipient)).willReturn(ENDPOINT);
    given(factory.newRestTemplate()).willReturn(template);
    given(template.postForEntity(eq(ENDPOINT), any(), eq(String.class))).willReturn(response);
  }

  private void verifySending() {
    verify(template).postForEntity(eq(ENDPOINT), requestCaptor.capture(), eq(String.class));
    assertThat(requestCaptor.getAllValues()).hasSize(1);

    HttpEntity<String> actual = requestCaptor.getValue();
    assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_XML);
    assertThat(actual.getBody()).isEqualTo(SIGNED_XML);
  }

  @Test
  void attemptToSendMessage_http_nok() {
    mockSerialisation();
    mockSending();
    given(response.getStatusCode()).willReturn(HttpStatus.BAD_REQUEST);
    given(response.getBody()).willReturn(BODY_ERROR_MESSAGE);

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertExceptionCauseInstanceOf(actual, "Failed to send message to recipient at ENDPOINT", UftpConnectorException.class);
    UftpConnectorException rootCause = (UftpConnectorException) actual.getCause();
    assertException(rootCause, "Response status code: 400 - Details: BODY_ERROR_MESSAGE");
  }
}