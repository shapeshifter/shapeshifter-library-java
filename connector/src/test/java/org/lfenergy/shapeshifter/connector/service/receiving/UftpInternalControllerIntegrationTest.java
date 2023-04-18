// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.receiving;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.lfenergy.shapeshifter.api.USEFRoleType.AGR;
import static org.lfenergy.shapeshifter.api.USEFRoleType.DSO;
import static org.lfenergy.shapeshifter.connector.model.PayloadMessageFixture.createTestFlexRequest;
import static org.lfenergy.shapeshifter.connector.model.PayloadMessageFixture.createTestMessage;
import static org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoFixture.TEST_PRIVATE_KEY;
import static org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoFixture.TEST_PUBLIC_KEY;
import static org.lfenergy.shapeshifter.connector.service.participant.UftpParticipantFixture.AGR_DOMAIN;
import static org.lfenergy.shapeshifter.connector.service.participant.UftpParticipantFixture.DSO_DOMAIN;
import static org.lfenergy.shapeshifter.connector.service.participant.UftpParticipantFixture.createTestAGRParticipantInformation;
import static org.lfenergy.shapeshifter.connector.service.participant.UftpParticipantFixture.createTestDSOParticipant;
import static org.lfenergy.shapeshifter.connector.service.participant.UftpParticipantFixture.createTestDSOParticipantInformation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.api.AcceptedRejectedType;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.connector.application.UftpControllerTestApp;
import org.lfenergy.shapeshifter.connector.model.SigningDetails;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.UftpParticipantService;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.handler.UftpPayloadHandler;
import org.lfenergy.shapeshifter.connector.service.sending.UftpSendMessageService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.connector.service.validation.CongestionPointSupport;
import org.lfenergy.shapeshifter.connector.service.validation.ContractSupport;
import org.lfenergy.shapeshifter.connector.service.validation.ParticipantSupport;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = UftpControllerTestApp.class)
@AutoConfigureWireMock(port = 0)
class UftpInternalControllerIntegrationTest {

  private static final String HTTP_LOCALHOST = "http://localhost:";
  private static final String APPLICATION_ENDPOINT_PATH = "/shapeshifter/api/v3/message";
  private static final String WIREMOCK_ENDPOINT_PATH = "/sender/api/v3/messages";
  private static final String XML_CONTENT_TYPE = "text/xml";
  private static final String MESSAGE_ID = UUID.randomUUID().toString();
  private static final UftpParticipant DSO_PARTICIPANT = createTestDSOParticipant();

  @LocalServerPort
  private int serverPort;

  @MockBean
  UftpPayloadHandler uftpPayloadHandler;
  @MockBean
  UftpParticipantService uftpParticipantService;
  @MockBean
  UftpValidatorSupport validatorSupport;
  @MockBean
  ParticipantSupport participantSupport;
  @MockBean
  UftpMessageSupport messageSupport;
  @MockBean
  ContractSupport contractSupport;
  @MockBean
  CongestionPointSupport congestionPointSupport;
  @Autowired
  private WireMockServer wireMockServer;
  @Autowired
  private TestRestTemplate restTemplate;
  @Autowired
  UftpSendMessageService uftpSendMessageService;
  @Autowired
  private UftpSerializer serializer;
  @Autowired
  private UftpCryptoService cryptoService;
  @Autowired
  private UftpReceivedMessageService receivedMessageService;

  @BeforeEach
  void setup() {
    stubFor(post(urlEqualTo(WIREMOCK_ENDPOINT_PATH)).willReturn(aResponse().withStatus(200)));

    doAnswer(invocation -> {
      var uftpParticipant = (UftpParticipant) invocation.getArgument(0);
      var payloadMessage = (PayloadMessageType) invocation.getArgument(1);

      receivedMessageService.process(uftpParticipant, payloadMessage);
      return null;
    }).when(uftpPayloadHandler).notifyNewIncomingMessage(any(UftpParticipant.class), any(PayloadMessageType.class));

    doAnswer(invocation -> {
      var sender = (UftpParticipant) invocation.getArgument(0);
      var payloadMessage = (PayloadMessageType) invocation.getArgument(1);

      var recipientRole = sender.role() == DSO ? AGR : DSO;
      var recipient = new UftpParticipant(payloadMessage.getRecipientDomain(), recipientRole);
      var signingDetails = new SigningDetails(sender, TEST_PRIVATE_KEY, recipient);

      uftpSendMessageService.attemptToSendMessage(payloadMessage, signingDetails);
      return null;
    }).when(uftpPayloadHandler).notifyNewOutgoingMessage(any(UftpParticipant.class), any(PayloadMessageType.class));
  }

  @AfterEach
  void tearDown() {
    WireMock.reset();
  }

  @Test
  void postUftpMessage_flexRequest_happyFlow() throws Exception {
    setupUftpDetailsMock();
    setupValidatorSupport();

    var flexRequest = createTestFlexRequest(MESSAGE_ID, DSO_DOMAIN, AGR_DOMAIN);
    var response = restTemplate.postForEntity(createSignedMessageEndpointURI(), createSignedMessageHttpRequest(DSO_PARTICIPANT, flexRequest), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    await().atMost(5, SECONDS).untilAsserted(() -> verify(exactly(1), postRequestedFor(urlEqualTo(WIREMOCK_ENDPOINT_PATH))));
    var postedRequests = findAll(postRequestedFor(urlEqualTo(WIREMOCK_ENDPOINT_PATH)));
    var responseXML = postedRequests.get(0).getBodyAsString();

    var result = (FlexRequestResponse) unwrapSignedMessage(responseXML);

    assertThat(result.getFlexRequestMessageID()).isEqualTo(flexRequest.getMessageID());
    assertThat(result.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
    assertThat(result.getRejectionReason()).isBlank();
    assertThat(result.getSenderDomain()).isEqualTo(AGR_DOMAIN);
    assertThat(result.getRecipientDomain()).isEqualTo(DSO_DOMAIN);
  }

  @Test
  void postUftpMessage_flexRequest_duplicate() throws Exception {
    setupUftpDetailsMock();
    setupValidatorSupport();

    var flexRequest = createTestFlexRequest(MESSAGE_ID, DSO_DOMAIN, AGR_DOMAIN);
    given(messageSupport.getPreviousMessage(flexRequest.getMessageID(), flexRequest.getRecipientDomain())).willReturn(Optional.of(flexRequest));

    var response = restTemplate.postForEntity(createSignedMessageEndpointURI(), createSignedMessageHttpRequest(DSO_PARTICIPANT, flexRequest), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void postUftpMessage_flexRequest_validationError() throws Exception {
    setupUftpDetailsMock();
    setupValidatorSupport();

    var flexRequest = createTestFlexRequest(MESSAGE_ID, DSO_DOMAIN, AGR_DOMAIN);
    flexRequest.setISPDuration(Duration.ofMinutes(30));
    var response = restTemplate.postForEntity(createSignedMessageEndpointURI(), createSignedMessageHttpRequest(DSO_PARTICIPANT, flexRequest), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    await().atMost(5, SECONDS).untilAsserted(() -> verify(exactly(1), postRequestedFor(urlEqualTo(WIREMOCK_ENDPOINT_PATH))));
    var postedRequests = findAll(postRequestedFor(urlEqualTo(WIREMOCK_ENDPOINT_PATH)));
    var responseXML = postedRequests.get(0).getBodyAsString();

    var result = (FlexRequestResponse) unwrapSignedMessage(responseXML);

    assertThat(result.getFlexRequestMessageID()).isEqualTo(flexRequest.getMessageID());
    assertThat(result.getResult()).isEqualTo(AcceptedRejectedType.REJECTED);
    assertThat(result.getRejectionReason()).isEqualTo("ISP duration rejected");
    assertThat(result.getSenderDomain()).isEqualTo(AGR_DOMAIN);
    assertThat(result.getRecipientDomain()).isEqualTo(DSO_DOMAIN);
  }

  @Test
  void postUftpMessage_testMessage_happyFlow() throws Exception {
    setupUftpDetailsMock();
    setupValidatorSupport();

    var testMessage = createTestMessage(MESSAGE_ID, DSO_DOMAIN, AGR_DOMAIN);
    var response = restTemplate.postForEntity(createSignedMessageEndpointURI(), createSignedMessageHttpRequest(DSO_PARTICIPANT, testMessage), String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    await().atMost(5, SECONDS).untilAsserted(() -> verify(exactly(1), postRequestedFor(urlEqualTo(WIREMOCK_ENDPOINT_PATH))));
    var postedRequests = findAll(postRequestedFor(urlEqualTo(WIREMOCK_ENDPOINT_PATH)));
    var responseXML = postedRequests.get(0).getBodyAsString();

    var result = (TestMessageResponse) unwrapSignedMessage(responseXML);

    assertThat(result.getSenderDomain()).isEqualTo(AGR_DOMAIN);
    assertThat(result.getRecipientDomain()).isEqualTo(DSO_DOMAIN);
    assertThat(result.getConversationID()).isEqualTo(testMessage.getConversationID());
  }

  private void setupUftpDetailsMock() {
    var agrParticipantInformation = createTestAGRParticipantInformation(TEST_PUBLIC_KEY, getMockEndpointURL());
    var dsoParticipantInformation = createTestDSOParticipantInformation(TEST_PUBLIC_KEY, getMockEndpointURL());

    given(uftpParticipantService.getParticipantInformation(AGR, AGR_DOMAIN)).willReturn(Optional.of(agrParticipantInformation));
    given(uftpParticipantService.getParticipantInformation(DSO, DSO_DOMAIN)).willReturn(Optional.of(dsoParticipantInformation));
  }

  private void setupValidatorSupport() {
    given(participantSupport.isHandledRecipient(new UftpParticipant(AGR_DOMAIN, AGR))).willReturn(true);
    given(participantSupport.isHandledRecipient(new UftpParticipant(DSO_DOMAIN, DSO))).willReturn(true);
    given(participantSupport.isAllowedSender(any(UftpParticipant.class))).willReturn(true);
    given(contractSupport.isSupportedContractID(any(String.class))).willReturn(true);
    given(congestionPointSupport.areKnownCongestionPoints(any(Collection.class))).willReturn(true);
    given(validatorSupport.isSupportedIspDuration(Duration.ofMinutes(15))).willReturn(true);
    given(validatorSupport.isSupportedTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"))).willReturn(true);
  }

  private HttpEntity<String> createSignedMessageHttpRequest(UftpParticipant sender, PayloadMessageType payloadMessage) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("CONTENT-TYPE", XML_CONTENT_TYPE);
    var flexRequestXml = serializer.toXml(payloadMessage);

    return new HttpEntity<>(wrapInSignedMessage(sender, flexRequestXml), headers);
  }

  private String wrapInSignedMessage(UftpParticipant sender, String content) {
    return serializer.toXml(cryptoService.signMessage(content, sender, TEST_PRIVATE_KEY));
  }

  private URI createSignedMessageEndpointURI() throws URISyntaxException {
    var baseUrl = HTTP_LOCALHOST + serverPort + APPLICATION_ENDPOINT_PATH;
    return new URI(baseUrl);
  }

  private String getMockEndpointURL() {
    return wireMockServer.url(WIREMOCK_ENDPOINT_PATH);
  }

  private PayloadMessageType unwrapSignedMessage(String content) {
    var signedMessage = serializer.fromSignedXml(content);
    var payloadMessageXML = cryptoService.verifySignedMessage(signedMessage, TEST_PUBLIC_KEY);
    return serializer.fromPayloadXml(payloadMessageXML);
  }

}