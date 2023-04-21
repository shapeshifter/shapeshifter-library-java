// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.sending;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.lfenergy.shapeshifter.connector.model.PayloadMessageFixture.createTestFlexRequest;
import static org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoFixture.TEST_PRIVATE_KEY;
import static org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoFixture.TEST_PUBLIC_KEY;
import static org.lfenergy.shapeshifter.connector.service.participant.UftpParticipantFixture.AGR_DOMAIN;
import static org.lfenergy.shapeshifter.connector.service.participant.UftpParticipantFixture.DSO_DOMAIN;
import static org.mockito.BDDMockito.given;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.application.UftpControllerTestApp;
import org.lfenergy.shapeshifter.connector.model.SigningDetails;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidationService;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = UftpControllerTestApp.class)
@AutoConfigureWireMock(port = 0)
@ExtendWith(MockitoExtension.class)
class UftpSendMessageServiceIntegrationTest {

  private static final String MESSAGE_ID = UUID.randomUUID().toString();
  private static final String SENDER_DOMAIN = "sender.domain.nl";
  private static final String RECIPIENT_DOMAIN = "recipient.domain.nl";
  private static final String RECIPIENT_ENDPOINT_PATH = "/uftp-endpoint";

  @Autowired
  private WireMockServer wireMockServer;

  @MockBean
  UftpValidationService uftpValidationService;

  @MockBean
  ParticipantResolutionService participantResolutionService;

  @Autowired
  private UftpSerializer serializer;

  @Autowired
  private UftpCryptoService cryptoService;

  @Autowired
  private UftpSendMessageService testSubject;

  @AfterEach
  void reset() {
    wireMockServer.resetAll();
  }

  private void setupParticipantResolutionServiceMock() {
    given(participantResolutionService.getEndPointUrl(Mockito.any(UftpParticipant.class))).willReturn(getMockEndpointURL());
  }

  @Test
  void attemptToSendMessage_ok() {
    setupParticipantResolutionServiceMock();
    stubFor(post(urlEqualTo(RECIPIENT_ENDPOINT_PATH)).willReturn(aResponse().withStatus(200)));

    var payload = createTestFlexRequest(MESSAGE_ID, DSO_DOMAIN, AGR_DOMAIN);
    var signingDetails = mockSigningDetails();

    testSubject.attemptToSendMessage(payload, signingDetails);

    await().atMost(5, SECONDS).untilAsserted(() -> verify(exactly(1), postRequestedFor(urlEqualTo(RECIPIENT_ENDPOINT_PATH))));
    var postedRequests = findAll(postRequestedFor(urlEqualTo(RECIPIENT_ENDPOINT_PATH)));
    var responseXML = postedRequests.get(0).getBodyAsString();
    var result = (FlexRequest) unwrapSignedMessage(responseXML);

    verifySentFlexRequestIsAsExpected(result, payload);
  }

  @Test
  void attemptToSendMessage_badRequest() {
    setupParticipantResolutionServiceMock();
    stubFor(post(urlEqualTo(RECIPIENT_ENDPOINT_PATH)).willReturn(aResponse().withStatus(400)));

    var payload = createTestFlexRequest(MESSAGE_ID, DSO_DOMAIN, AGR_DOMAIN);
    var signingDetails = mockSigningDetails();

    assertThatThrownBy(() -> testSubject.attemptToSendMessage(payload, signingDetails))
        .isInstanceOfSatisfying(UftpClientErrorException.class, e ->
            assertThat(e.getHttpStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST))
        .hasMessage("Failed to send message to " + RECIPIENT_DOMAIN + " at " + wireMockServer.url(RECIPIENT_ENDPOINT_PATH));

    await().atMost(5, SECONDS).untilAsserted(() -> verify(exactly(1), postRequestedFor(urlEqualTo(RECIPIENT_ENDPOINT_PATH))));
    var postedRequests = findAll(postRequestedFor(urlEqualTo(RECIPIENT_ENDPOINT_PATH)));
    var responseXML = postedRequests.get(0).getBodyAsString();
    var result = (FlexRequest) unwrapSignedMessage(responseXML);

    verifySentFlexRequestIsAsExpected(result, payload);
  }

  private void verifySentFlexRequestIsAsExpected(FlexRequest result, FlexRequest expected) {
    assertThat(result.getMessageID()).isEqualTo(expected.getMessageID());
    assertThat(result.getRecipientDomain()).isEqualTo(expected.getRecipientDomain());
    assertThat(result.getSenderDomain()).isEqualTo(expected.getSenderDomain());
    assertThat(result.getContractID()).isEqualTo(expected.getContractID());
    assertThat(result.getExpirationDateTime()).isEqualTo(expected.getExpirationDateTime());
    assertThat(result.getRevision()).isEqualTo(expected.getRevision());
    assertThat(result.getISPDuration()).isEqualTo(expected.getISPDuration());
    assertThat(result.getISPS()).hasSize(expected.getISPS().size());
    assertThat(result.getISPS()).usingRecursiveComparison().isEqualTo(expected.getISPS());
  }

  private SigningDetails mockSigningDetails() {
    var sender = mockUftpParticipant(SENDER_DOMAIN, USEFRoleType.DSO);
    var recipient = mockUftpParticipant(RECIPIENT_DOMAIN, USEFRoleType.AGR);

    return new SigningDetails(sender, TEST_PRIVATE_KEY, recipient);
  }

  private UftpParticipant mockUftpParticipant(String domain, USEFRoleType role) {
    return new UftpParticipant(domain, role);
  }

  private PayloadMessageType unwrapSignedMessage(String content) {
    var signedMessage = serializer.fromSignedXml(content);
    var payloadMessageXML = cryptoService.verifySignedMessage(signedMessage, TEST_PUBLIC_KEY);
    return serializer.fromPayloadXml(payloadMessageXML);
  }

  private String getMockEndpointURL() {
    return wireMockServer.url(RECIPIENT_ENDPOINT_PATH);
  }

}
