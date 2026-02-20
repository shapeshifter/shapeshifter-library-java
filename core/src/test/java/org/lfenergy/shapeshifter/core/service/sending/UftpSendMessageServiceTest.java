// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.sending;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.api.model.UftpParticipantInformation;
import org.lfenergy.shapeshifter.core.common.HttpStatusCode;
import org.lfenergy.shapeshifter.core.model.SigningDetails;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.ParticipantAuthorizationProvider;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.core.service.participant.UftpParticipantInformationBuilder;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class UftpSendMessageServiceTest {

    private static final String PAYLOAD_XML = "PAYLOAD_XML";
    private static final String PRIVATE_KEY = "PRIVATE_KEY";
    private static final String SIGNED_XML = "SIGNED_XML";
    private static final String DOMAIN = "DOMAIN";
    private static final String PATH_HAPPY_FLOW = "/happy-flow/";
    private static final String PATH_204_NO_CONTENT = "/204-no-content/";
    private static final String PATH_BAD_REQUEST = "/bad-request/";
    private static final String PATH_3XX = "/3xx/";
    private static final String PATH_FORBIDDEN = "/forbidden/";
    private static final String PATH_INTERNAL_SERVER_ERROR = "/internal-server-error/";

    private static WireMockServer wireMockServer;

    private final UftpParticipantInformationBuilder uftpParticipantInformationBuilder = new UftpParticipantInformationBuilder();

    private final UftpParticipant recipient = new UftpParticipant(DOMAIN, USEFRoleType.AGR);

    private static final Faker FAKER = new Faker(Locale.forLanguageTag("nl"));

    @Mock
    private UftpSerializer serializer;
    @Mock
    private UftpCryptoService cryptoService;
    @Mock
    private ParticipantResolutionService participantService;
    @Mock
    private ParticipantAuthorizationProvider authorizationProvider;
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

    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeAll
    public static void setupWireMockServer() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());
    }

    @BeforeEach
    void setup() {
        wireMockServer.resetAll();
        setupWiremockStubs();
        this.testSubject = new UftpSendMessageService(serializer, cryptoService, participantService, authorizationProvider, uftpValidationService);
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

    private void verifyNoValidations() {
        verifyNoInteractions(uftpValidationService);
    }

    @Test
    void attemptToSendMessage_happyFlow_withAuthorization() {
        mockSerialisation();
        mockSending();
        mockParticipantServiceWithAuthorization(getEndpointURL(PATH_HAPPY_FLOW));

        String authorizationHeader = FAKER.regexify("Bearer [\\w\\d]{20}");
        given(authorizationProvider.getAuthorizationHeader(any(UftpParticipant.class))).willReturn(authorizationHeader);

        testSubject.attemptToSendMessage(flexRequest, details);

        wireMockServer.verify(1,  postRequestedFor(urlPathEqualTo(PATH_HAPPY_FLOW)).withHeader("Authorization", equalTo(authorizationHeader)));
        verifyNoValidations();
    }

    @Test
    void attemptToSendMessage_happyFlow() {
        mockSerialisation();
        mockSending();
        mockParticipantServiceWithoutAuthorization(getEndpointURL(PATH_HAPPY_FLOW));

        testSubject.attemptToSendMessage(flexRequest, details);

        wireMockServer.verify(1,  postRequestedFor( urlPathEqualTo(PATH_HAPPY_FLOW)).withoutHeader("Authorization"));
        verifyNoValidations();
    }

    @Test
    void attemptToSendMessage_204_No_Content() {
        mockSerialisation();
        mockSending();
        mockParticipantServiceWithoutAuthorization(getEndpointURL(PATH_204_NO_CONTENT));

        testSubject.attemptToSendMessage(flexRequest, details);

        wireMockServer.verify(1,  postRequestedFor( urlPathEqualTo(PATH_204_NO_CONTENT)));
        verifyNoValidations();
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatusCode.class, names = {"TEMPORARY_REDIRECT", "PERMANENT_REDIRECT"})
    void attemptToSendMessage_3xx_followRedirect(HttpStatusCode statusCode) {
        mockSerialisation();
        mockSending();

        stubFor(post(urlPathMatching(PATH_3XX + ".*"))
                .willReturn(aResponse()
                        .withStatus(statusCode.getValue())
                        .withHeader("Location", getEndpointURL(PATH_HAPPY_FLOW))));

        mockParticipantServiceWithoutAuthorization(getEndpointURL(PATH_3XX));

        testSubject.attemptToSendMessage(flexRequest, details);

        wireMockServer.verify(1, postRequestedFor(urlPathMatching(PATH_3XX + ".*")));
        wireMockServer.verify(1, postRequestedFor(urlPathMatching(PATH_HAPPY_FLOW)));
        verifyNoValidations();
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatusCode.class, names = {"TEMPORARY_REDIRECT", "PERMANENT_REDIRECT"})
    void attemptToSendMessage_3xx_followRedirect_thenError(HttpStatusCode statusCode) {
        mockSerialisation();
        mockSending();

        stubFor(post(urlPathMatching(PATH_3XX + ".*"))
                .willReturn(aResponse()
                        .withStatus(statusCode.getValue())
                        .withHeader("Location", getEndpointURL(PATH_BAD_REQUEST))));

        mockParticipantServiceWithoutAuthorization(getEndpointURL(PATH_3XX));

        var actual = assertThrows(UftpClientErrorException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        wireMockServer.verify(1, postRequestedFor(urlPathMatching(PATH_3XX + ".*")));
        wireMockServer.verify(1, postRequestedFor(urlPathMatching(PATH_BAD_REQUEST)));
        assertThat(actual)
                .isInstanceOf(UftpClientErrorException.class)
                .hasMessage("Client error 400 received while sending UFTP message to " + getEndpointURL(PATH_BAD_REQUEST) + ": Bad Request");
        assertThat(actual.getHttpStatusCode()).isEqualTo(HttpStatusCode.BAD_REQUEST);
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatusCode.class, names = {"TEMPORARY_REDIRECT", "PERMANENT_REDIRECT"})
    void attemptToSendMessage_3xx_followRedirect_locationHeaderMissing(HttpStatusCode statusCode) {
        mockSerialisation();
        mockSending();

        stubFor(post(urlPathMatching(PATH_3XX + ".*"))
                .willReturn(aResponse()
                        .withStatus(statusCode.getValue())));

        mockParticipantServiceWithoutAuthorization(getEndpointURL(PATH_3XX));

        var actual = assertThrows(UftpServerErrorException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        assertThat(actual)
                .isInstanceOf(UftpServerErrorException.class)
                .hasMessage("Redirect received without Location header while sending UFTP message to " + getEndpointURL(PATH_3XX));
        assertThat(actual.getHttpStatusCode()).isEqualTo(statusCode);
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatusCode.class, names = {"TEMPORARY_REDIRECT", "PERMANENT_REDIRECT"})
    void attemptToSendMessage_3xx_followRedirect_tooManyRedirects(HttpStatusCode statusCode) {
        mockSerialisation();
        mockSending();

        stubFor(post(urlPathMatching(PATH_3XX + ".*"))
                .willReturn(aResponse()
                        .withStatus(statusCode.getValue())
                        .withHeader("Location", getEndpointURL(PATH_3XX))));

        var endpoint = getEndpointURL(PATH_3XX);
        mockParticipantServiceWithoutAuthorization(endpoint);

        var actual = assertThrows(UftpSendException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        wireMockServer.verify(3, postRequestedFor(urlPathMatching(PATH_3XX + ".*")));
        assertThat(wireMockServer.findAllUnmatchedRequests()).isEmpty();

        assertThat(actual)
                .isInstanceOf(UftpSendException.class)
                .hasMessage("Too many redirects while sending UFTP message to " + endpoint);
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatusCode.class, names = {"MOVED_PERMANENTLY", "FOUND", "SEE_OTHER", "MULTIPLE_CHOICES", "USE_PROXY", "NOT_MODIFIED"})
    void attemptToSendMessage_3xx_doNotFollowRedirect(HttpStatusCode statusCode) {
        mockSerialisation();
        mockSending();

        stubFor(post(urlPathMatching(PATH_3XX + ".*"))
                .willReturn(aResponse()
                        .withStatus(statusCode.getValue())
                        .withHeader("Location", getEndpointURL(PATH_HAPPY_FLOW))));

        var endpoint = getEndpointURL(PATH_3XX);
        mockParticipantServiceWithoutAuthorization(endpoint);

        var actual = assertThrows(UftpSendException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        assertThat(actual)
                .isInstanceOf(UftpSendException.class)
                .hasMessageContaining("Unexpected response status " + statusCode.getValue() + " received while sending UFTP message to " + endpoint);
        assertThat(actual.getHttpStatusCode()).isEqualTo(statusCode);
    }

    @Test
    void attemptToSendMessage_internalServerError() {
        mockSerialisation();
        mockSending();
        var endpoint = getEndpointURL(PATH_INTERNAL_SERVER_ERROR);
        mockParticipantServiceWithoutAuthorization(endpoint);
        var httpStatusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;

        var actual = assertThrows(UftpSendException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        assertThat(actual)
                .isInstanceOf(UftpSendException.class)
                .hasMessage("Server error 500 received while sending UFTP message to " + endpoint + ": Internal Server Error");
        assertThat(actual.getHttpStatusCode()).isEqualTo(httpStatusCode);
    }

    @Test
    void attemptToSendMessage_ResponseOnBadRequest() {
        mockSerialisation();
        mockSending();
        var endpoint = getEndpointURL(PATH_BAD_REQUEST);
        mockParticipantServiceWithoutAuthorization(endpoint);

        var httpStatusCode = HttpStatusCode.BAD_REQUEST;

        var actual = assertThrows(UftpClientErrorException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        assertThat(actual)
                .isInstanceOf(UftpClientErrorException.class)
                .hasMessage("Client error 400 received while sending UFTP message to " + endpoint + ": Bad Request");
        assertThat(actual.getHttpStatusCode()).isEqualTo(httpStatusCode);
    }

    @Test
    void attemptToSendMessage_UrlIsNull() {
        mockSerialisation();
        mockSending();
        mockParticipantServiceWithoutAuthorization(null);
        var actual = assertThrows(NullPointerException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        assertThat(actual).isInstanceOf(NullPointerException.class);
    }

    @Test
    void attemptToSendMessage_MalformedUrl() {
        mockSerialisation();
        mockSending();

        var endpoint = "http://???";
        mockParticipantServiceWithoutAuthorization(endpoint);
        var actual = assertThrows(UftpSendException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        assertThat(actual)
                .isInstanceOf(UftpSendException.class)
                .hasMessage("Could not send UFTP message; invalid endpoint: unsupported URI " + endpoint);
        assertThat(actual.getHttpStatusCode()).isEqualTo(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    void attemptToSendMessage_connectFailed() {
        mockSerialisation();
        mockSending();
        var endpoint = "http://localhost:1"; // something that will trigger a java.net.ConnectException
        mockParticipantServiceWithoutAuthorization(endpoint);
        var httpStatusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;

        var actual = assertThrows(UftpSendException.class, () ->
                testSubject.attemptToSendMessage(flexRequest, details));

        verifyNoValidations();

        assertThat(actual)
                .isInstanceOf(UftpSendException.class)
                .hasMessage("Unexpected I/O exception while sending UFTP message to " + endpoint + ": ConnectException: null");
        assertThat(actual.getHttpStatusCode()).isEqualTo(httpStatusCode);
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
        mockParticipantServiceWithoutAuthorization(getEndpointURL(PATH_HAPPY_FLOW));

        // We are calling attemptToValidateAndSendMessage, but since we are sending a flex request response, the
        // message is not validated (since we have decided not to validate outgoing response messages)
        testSubject.attemptToValidateAndSendMessage(flexRequestResponse, details);
        verifyNoValidations();
    }

    @Test
    void attemptToSendMessage_appliesReadTimeoutToRequest() throws IOException, InterruptedException {
        mockSerialisation();
        mockSending();
        mockParticipantServiceWithoutAuthorization(getEndpointURL(PATH_HAPPY_FLOW));

        var readTimeout = Duration.ofSeconds(42);

        testSubject = new UftpSendMessageService(serializer, cryptoService, participantService, authorizationProvider, uftpValidationService, httpClient);

        testSubject.addRequestInterceptor(request -> request.timeout(readTimeout));

        given(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).willReturn(httpResponse);
        given(httpResponse.statusCode()).willReturn(200);

        testSubject.attemptToSendMessage(flexRequest, details);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any());

        HttpRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.timeout()).isPresent().contains(readTimeout);
    }

    @Test
    void attemptToSendMessage_noTimeoutByDefault() throws IOException, InterruptedException {
        mockSerialisation();
        mockSending();
        mockParticipantServiceWithoutAuthorization(getEndpointURL(PATH_HAPPY_FLOW));

        testSubject = new UftpSendMessageService(serializer, cryptoService, participantService, authorizationProvider, uftpValidationService, httpClient);

        given(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).willReturn(httpResponse);
        given(httpResponse.statusCode()).willReturn(200);

        testSubject.attemptToSendMessage(flexRequest, details);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any());

        assertThat(requestCaptor.getValue().timeout()).isEmpty();
    }


    private void mockParticipantServiceWithAuthorization(String endpointUrl) {
        UftpParticipantInformation recipientInformation = uftpParticipantInformationBuilder.withEndpoint(endpointUrl).withRequiresAuthorization(true).build();
        given(participantService.getParticipantInformation(any(UftpParticipant.class))).willReturn(recipientInformation);
    }

    private void mockParticipantServiceWithoutAuthorization(String endpointUrl) {
        UftpParticipantInformation recipientInformation = uftpParticipantInformationBuilder.withEndpoint(endpointUrl).withRequiresAuthorization(false).build();
        given(participantService.getParticipantInformation(any(UftpParticipant.class))).willReturn(recipientInformation);
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
    }
}