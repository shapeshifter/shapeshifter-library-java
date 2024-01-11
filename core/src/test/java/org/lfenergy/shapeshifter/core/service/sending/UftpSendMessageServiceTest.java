// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.sending;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.common.HttpStatusCode;
import org.lfenergy.shapeshifter.core.model.SigningDetails;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpSendMessageServiceTest {

  private static final String PAYLOAD_XML = "PAYLOAD_XML";
  private static final String PRIVATE_KEY = "PRIVATE_KEY";
  private static final String SIGNED_XML = "SIGNED_XML";
  private static final String DOMAIN = "DOMAIN";
  private static final String PATH_HAPPY_FLOW = "/happy-flow/";
  private static final String PATH_204_NO_CONTENT = "/204-no-content/";
  private static final String PATH_BAD_REQUEST = "/bad-request/";
  private static final String PATH_302_FOUND = "/302-found/";
  private static final String PATH_FORBIDDEN = "/forbidden/";
  private static final String PATH_INTERNAL_SERVER_ERROR = "/internal-server-error/";

  private static WireMockServer wireMockServer;

  private final UftpParticipant recipient = new UftpParticipant(DOMAIN, USEFRoleType.AGR);

  @Mock
  private UftpSerializer serializer;
  @Mock
  private UftpCryptoService cryptoService;
  @Mock
  private ParticipantResolutionService participantService;
  @Mock
  private UftpValidationService uftpValidationService;

  private UftpSendMessageService testSubject;

  @Mock
  private FlexRequest flexRequest;
  @Mock
  private SigningDetails details;
  @Mock
  private UftpParticipant sender;
  @Mock
  private SignedMessage signedMessage;

  @BeforeAll
  public static void setupWireMockServer() {
    wireMockServer = new WireMockServer(0);
    wireMockServer.start();
    WireMock.configureFor(wireMockServer.port());
    setupWiremockStubs();
  }

  @BeforeEach
  void setup() {
    this.testSubject = new UftpSendMessageService(serializer, cryptoService, participantService, uftpValidationService);
  }

  @AfterEach
  public void noMore() {
    verifyNoMoreInteractions(
        serializer,
        cryptoService,
        participantService,
        flexRequest,
        details,
        sender,
        signedMessage
    );


  }

  @AfterAll
  public static void tearDownWireMockServer() {
    wireMockServer.resetAll();
    wireMockServer.stop();
  }


  private void mockSerialisation() {
    given(serializer.toXml(flexRequest)).willReturn(PAYLOAD_XML);
    given(details.sender()).willReturn(sender);
    given(details.senderPrivateKey()).willReturn(PRIVATE_KEY);
    given(cryptoService.signMessage(PAYLOAD_XML, sender, PRIVATE_KEY)).willReturn(signedMessage);
    given(serializer.toXml(signedMessage)).willReturn(SIGNED_XML);
  }

  private void mockSending() {
    given(details.recipient()).willReturn(recipient);
  }

  private void verifySending() {
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToSendMessage_happyFlow() {
    mockSerialisation();
    mockSending();
    given(participantService.getEndPointUrl(any())).willReturn(getEndpointURL(PATH_HAPPY_FLOW));

    testSubject.attemptToSendMessage(flexRequest, details);

    verifySending();
  }

  @Test
  void attemptToSendMessage_204_No_Content() {
    mockSerialisation();
    mockSending();
    var endpoint = getEndpointURL(PATH_204_NO_CONTENT);
    given(participantService.getEndPointUrl(any())).willReturn(endpoint);

    testSubject.attemptToSendMessage(flexRequest, details);

    verifySending();
  }

  @Test
  void attemptToSendMessage_302_Found() {
    mockSerialisation();
    mockSending();
    var endpoint = getEndpointURL(PATH_302_FOUND);
    given(participantService.getEndPointUrl(any())).willReturn(endpoint);

    var actual = assertThrows(UftpSendException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertThat(actual)
        .isInstanceOf(UftpSendException.class)
        .hasMessage("Unexpected response status 302 received while sending UFTP message to " + endpoint + ": Found");
    assertThat(actual.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToSendMessage_internalServerError() {
    mockSerialisation();
    mockSending();
    var endpoint = getEndpointURL(PATH_INTERNAL_SERVER_ERROR);
    given(participantService.getEndPointUrl(any())).willReturn(endpoint);
    var httpStatusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;

    var actual = assertThrows(UftpSendException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertThat(actual)
        .isInstanceOf(UftpSendException.class)
        .hasMessage("Server error 500 received while sending UFTP message to " + endpoint + ": Internal Server Error");
    assertThat(actual.getHttpStatusCode()).isEqualTo(httpStatusCode);
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToSendMessage_ResponseOnBadRequest() {
    mockSerialisation();
    mockSending();
    var endpoint = getEndpointURL(PATH_BAD_REQUEST);
    given(participantService.getEndPointUrl(any())).willReturn(endpoint);

    var httpStatusCode = HttpStatusCode.BAD_REQUEST;

    var actual = assertThrows(UftpClientErrorException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertThat(actual)
        .isInstanceOf(UftpClientErrorException.class)
        .hasMessage("Client error 400 received while sending UFTP message to " + endpoint + ": Bad Request");
    assertThat(actual.getHttpStatusCode()).isEqualTo(httpStatusCode);
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToSendMessage_UrlIsNull() {
    mockSerialisation();
    mockSending();

    given(participantService.getEndPointUrl(any())).willReturn(null);
    var actual = assertThrows(NullPointerException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertThat(actual)
        .isInstanceOf(NullPointerException.class);
    verifyNoInteractions(uftpValidationService);
  }

  @Test
  void attemptToSendMessage_MalformedUrl() {
    mockSerialisation();
    mockSending();

    var endpoint = "http://???";
    given(participantService.getEndPointUrl(any())).willReturn(endpoint);
    var actual = assertThrows(UftpSendException.class, () ->
        testSubject.attemptToSendMessage(flexRequest, details));

    verifySending();

    assertThat(actual)
        .isInstanceOf(UftpSendException.class)
        .hasMessage("Could not send UFTP message; invalid endpoint: unsupported URI " + endpoint);
    assertThat(actual.getHttpStatusCode()).isEqualTo(HttpStatusCode.INTERNAL_SERVER_ERROR);
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
    given(cryptoService.signMessage(PAYLOAD_XML, sender, PRIVATE_KEY)).willReturn(signedMessage);
    given(serializer.toXml(signedMessage)).willReturn(SIGNED_XML);

    mockSending();
    given(participantService.getEndPointUrl(any())).willReturn(getEndpointURL(PATH_HAPPY_FLOW));

    // We are calling attemptToValidateAndSendMessage, but since we are sending a flex request response, the
    // message is not validated (since we have decided not to validate outgoing response messages)
    testSubject.attemptToValidateAndSendMessage(flexRequestResponse, details);
    verifyNoInteractions(uftpValidationService);
    verifySending();
  }

  private String getEndpointURL(String path) {
    return wireMockServer.url(path);
  }

  private static void setupWiremockStubs() {
    stubFor(post(urlPathMatching(PATH_HAPPY_FLOW + ".*"))
                .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/xml")
                                .withBody("OK")));
    stubFor(post(urlPathMatching(PATH_204_NO_CONTENT + ".*"))
                .willReturn(aResponse().withStatus(204)));
    stubFor(post(urlPathMatching(PATH_BAD_REQUEST + ".*"))
                .willReturn(aResponse()
                                .withStatus(400)
                                .withHeader("Content-Type", "text/xml")
                                .withBody("Bad Request")));
    stubFor(post(urlPathMatching(PATH_FORBIDDEN + ".*"))
                .willReturn(aResponse()
                                .withStatus(403)
                                .withHeader("Content-Type", "text/xml")
                                .withBody("Forbidden")));
    stubFor(post(urlPathMatching(PATH_INTERNAL_SERVER_ERROR + ".*"))
                .willReturn(aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "text/xml")
                                .withBody("Internal Server Error")));
    stubFor(post(urlPathMatching(PATH_302_FOUND + ".*"))
                .willReturn(aResponse()
                                .withStatus(302)
                                .withHeader("Location", "https://some/url")
                                .withBody("Found")));
  }
}