package org.lfenergy.shapeshifter.connector.service.sending;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.model.ShippingDetails;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = UftpSendMessageService.class)
@ExtendWith(MockitoExtension.class)
class UftpSendMessageServiceIntegrationTest {

  private static final int WIRE_MOCK_HTTP_PORT_NUMBER = 18080;
  private static final WireMockConfiguration WIRE_MOCK_CONFIGURATION = WireMockConfiguration.wireMockConfig().port(WIRE_MOCK_HTTP_PORT_NUMBER);
  private static final String SENDER_DOMAIN = "sender.domain.nl";
  private static final String RECIPIENT_DOMAIN = "recipient.domain.nl";
  private static final String SENDER_PRIVATE_KEY = "senderPrivateKey";
  private static final String RECIPIENT_ENDPOINT_PATH = "/uftp-endpoint";
  private static final String RECIPIENT_ENDPOINT = "http://localhost:" + WIRE_MOCK_HTTP_PORT_NUMBER + RECIPIENT_ENDPOINT_PATH;
  public final static WireMockServer wireMock = new WireMockServer(WIRE_MOCK_CONFIGURATION);

  @MockBean
  UftpValidationService uftpValidationService;

  @MockBean
  UftpSerializer uftpSerializer;

  @MockBean
  UftpCryptoService uftpCryptoService;

  @MockBean
  ParticipantResolutionService participantResolutionService;

  @MockBean
  UftpSendFactory uftpSendFactory;

  @Autowired
  private UftpSendMessageService testSubject;

  @BeforeAll
  static void setUp() {
    wireMock.start();
  }

  @AfterAll
  static void tearDown() {
    wireMock.stop();
  }

  @AfterEach
  void reset() {
    wireMock.resetAll();
  }

  private void setupMessageServiceMockedBeans() {
    Mockito.when(uftpSendFactory.newRestTemplate()).thenReturn(new RestTemplate());
    Mockito.when(participantResolutionService.getEndPointUrl(Mockito.any(UftpParticipant.class))).thenReturn(RECIPIENT_ENDPOINT);
  }

  @Test
  void attemptToSendMessage_ok() {
    setupMessageServiceMockedBeans();
    wireMock.stubFor(post(urlEqualTo(RECIPIENT_ENDPOINT_PATH)).willReturn(ok()));

    var payload = mockFlexRequest();
    var shippingDetails = mockShippingDetails();

    testSubject.attemptToSendMessage(payload, shippingDetails);

    wireMock.verify(1, postRequestedFor(urlEqualTo(RECIPIENT_ENDPOINT_PATH)));
  }

  @Test
  void attemptToSendMessage_badRequest() {
    setupMessageServiceMockedBeans();
    wireMock.stubFor(post(urlEqualTo(RECIPIENT_ENDPOINT_PATH)).willReturn(badRequest()));

    var shippingDetails = mockShippingDetails();
    assertThatThrownBy(() -> testSubject.attemptToSendMessage(null, shippingDetails))
        .isInstanceOfSatisfying(UftpClientErrorException.class, e ->
            assertThat(e.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST))
        .hasMessage("Failed to send message to " + RECIPIENT_DOMAIN + " at " + wireMock.url(RECIPIENT_ENDPOINT_PATH));

    wireMock.verify(1, postRequestedFor(urlEqualTo(RECIPIENT_ENDPOINT_PATH)));
  }

  private ShippingDetails mockShippingDetails() {
    var sender = mockUftpParticipant(SENDER_DOMAIN, USEFRoleType.DSO);
    var recipient = mockUftpParticipant(RECIPIENT_DOMAIN, USEFRoleType.AGR);

    return new ShippingDetails(sender, SENDER_PRIVATE_KEY, recipient);
  }

  private UftpParticipant mockUftpParticipant(String domain, USEFRoleType role) {
    return new UftpParticipant(domain, role);
  }

  private FlexRequest mockFlexRequest() {
    var flexRequest = new FlexRequest();
    flexRequest.setContractID("CONTRACT_ID");
    flexRequest.setPeriod(OffsetDateTime.now());
    flexRequest.setSenderDomain(SENDER_DOMAIN);
    flexRequest.setRecipientDomain(RECIPIENT_DOMAIN);
    return flexRequest;
  }

}
