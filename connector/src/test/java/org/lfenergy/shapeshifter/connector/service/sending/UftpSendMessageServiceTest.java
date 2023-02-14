package org.lfenergy.shapeshifter.connector.service.sending;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.model.ShippingDetails;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.connector.service.validation.model.ValidationResult;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class UftpSendMessageServiceTest {

  private static final String PAYLOAD_XML = "PAYLOAD_XML";
  private static final String PRIVATE_KEY = "PRIVATE_KEY";
  private static final String SIGNED_XML = "SIGNED_XML";
  private static final String DOMAIN = "DOMAIN";
  private static final String ENDPOINT = "ENDPOINT";
  private static final String BODY_ERROR_MESSAGE = "BODY_ERROR_MESSAGE";

  private final UftpParticipant recipient = new UftpParticipant(DOMAIN, USEFRoleType.AGR);

  @Mock
  private UftpSerializer serializer;
  @Mock
  private UftpCryptoService cryptoService;
  @Mock
  private ParticipantResolutionService participantService;
  @Mock
  private UftpSendFactory factory;
  @Mock
  private UftpValidationService uftpValidationService;
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
        template,
        response
    );
  }

  @Test
  void attemptToSendMessage_ok() {
    mockSerialisation();
    mockSending();
    mockResponse();
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
  }

  private void mockResponse() {
    given(template.postForEntity(eq(ENDPOINT), any(), eq(String.class))).willReturn(response);
  }

  private void verifySending() {
    verify(template).postForEntity(eq(ENDPOINT), requestCaptor.capture(), eq(String.class));
    assertThat(requestCaptor.getAllValues()).hasSize(1);

    HttpEntity<String> actual = requestCaptor.getValue();
    assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_XML);
    assertThat(actual.getBody()).isEqualTo(SIGNED_XML);
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToSendMessage_http_nok() {
    mockSerialisation();
    mockSending();
    mockResponse();
    given(response.getStatusCode()).willReturn(HttpStatus.BAD_REQUEST);
    given(response.getBody()).willReturn(BODY_ERROR_MESSAGE);

    var actual = assertThrows(UftpSendException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertThat(actual)
        .isInstanceOf(UftpSendException.class)
        .hasMessage("Failed to send message to DOMAIN at ENDPOINT")
        .hasRootCauseMessage("Response status code: 400 - Details: BODY_ERROR_MESSAGE");
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToSendMessage_HttpServerErrorException() {
    mockSerialisation();
    mockSending();

    var httpServerErrorException = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    given(template.postForEntity(eq(ENDPOINT), any(), eq(String.class))).willThrow(httpServerErrorException);

    var actual = assertThrows(UftpSendException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertThat(actual)
        .isInstanceOf(UftpSendException.class)
        .hasMessage("Failed to send message to " + DOMAIN + " at " + ENDPOINT)
        .hasCause(httpServerErrorException)
        .hasRootCauseMessage("500 Internal Server Error");
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToSendMessage_HttpClientErrorException() {
    mockSerialisation();
    mockSending();

    var httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
    given(template.postForEntity(eq(ENDPOINT), any(), eq(String.class))).willThrow(httpClientErrorException);

    var actual = assertThrows(UftpClientErrorException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertThat(actual)
        .isInstanceOfSatisfying(UftpClientErrorException.class, e ->
            assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST))
        .hasMessage("Failed to send message to " + DOMAIN + " at " + ENDPOINT)
        .hasCause(httpClientErrorException);
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToValidateAndSendMessage_ValidationException() {
    given(details.sender()).willReturn(sender);

    var validationFailureMessage = "The message was incorrect";
    given(uftpValidationService.validate(any())).willReturn(new ValidationResult(false, validationFailureMessage));
    var actual = assertThrows(UftpSendException.class, () -> testSubject.attemptToValidateAndSendMessage(flexRequest, details));
    assertThat(actual).hasMessage("Could not send UFTP message; the outgoing FlexRequest message was not valid: " + validationFailureMessage);
  }

  @Test
  void attemptToValidateAndSendMessage_OutgoingResponseMessageShouldNotBeValidated() {
    var flexRequestResponse = mock(FlexRequestResponse.class);
    given(serializer.toXml(flexRequestResponse)).willReturn(PAYLOAD_XML);
    given(details.sender()).willReturn(sender);
    given(details.senderPrivateKey()).willReturn(PRIVATE_KEY);
    given(cryptoService.sealMessage(PAYLOAD_XML, sender, PRIVATE_KEY)).willReturn(signedMessage);
    given(serializer.toXml(signedMessage)).willReturn(SIGNED_XML);

    mockSending();
    mockResponse();
    given(response.getStatusCode()).willReturn(HttpStatus.OK);

    // We are calling attemptToValidateAndSendMessage, but since we are sending a flex request response, the
    // message is not validated (since we have decided not to validate outgoing response messages)
    testSubject.attemptToValidateAndSendMessage(flexRequestResponse, details);
    verifyNoInteractions(uftpValidationService);
    verifySending();
  }
}