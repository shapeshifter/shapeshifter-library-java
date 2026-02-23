
package org.lfenergy.shapeshifter.spring.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.core.service.ParticipantAuthorizationProvider;
import org.lfenergy.shapeshifter.core.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.core.service.sending.RequestInterceptor;
import org.lfenergy.shapeshifter.core.service.sending.UftpSendMessageService;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = ShapeshifterConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ShapeshifterConfigurationTest {

    @MockitoBean UftpSerializer uftpSerializer;
    @MockitoBean UftpCryptoService uftpCryptoService;
    @MockitoBean ParticipantResolutionService participantResolutionService;
    @MockitoBean ParticipantAuthorizationProvider participantAuthorizationProvider;
    @MockitoBean UftpValidationService uftpValidationService;
    @MockitoBean UftpMessageSupport uftpMessageSupport;
    @MockitoBean UftpErrorProcessor uftpErrorProcessor;


    @Nested
    @TestPropertySource(properties = "shapeshifter.http.connect-timeout=5s")
    class ConnectTimeoutPropertiesSet {

        @Autowired
        private ShapeshifterProperties properties;

        @Test
        void shouldConfigureConnectTimeout() throws Exception {
            var service = new ShapeshifterConfiguration(properties).uftpSendMessageService(
                    uftpSerializer, uftpCryptoService, participantResolutionService,
                    participantAuthorizationProvider, uftpValidationService);

            var httpClientField = UftpSendMessageService.class.getDeclaredField("httpClient");
            httpClientField.setAccessible(true);
            var httpClient = (HttpClient) httpClientField.get(service);

            assertThat(httpClient.connectTimeout()).isPresent().contains(Duration.ofSeconds(5));
        }
    }

    @Nested
    @TestPropertySource(properties = {"shapeshifter.http.read-timeout=10s"})
    class ReadTimeoutPropertiesSet {

        @Autowired
        private ShapeshifterProperties properties;

        @Test
        void shouldRegisterReadTimeoutInterceptor() throws Exception {
            var service = new ShapeshifterConfiguration(properties).uftpSendMessageService(
                    uftpSerializer, uftpCryptoService, participantResolutionService,
                    participantAuthorizationProvider, uftpValidationService);

            var requestBuilder = HttpRequest.newBuilder().uri(new URI("http://localhost"));
            var interceptorsField = UftpSendMessageService.class.getDeclaredField("requestInterceptors");
            interceptorsField.setAccessible(true);
            var interceptors = (List<RequestInterceptor>) interceptorsField.get(service);
            interceptors.forEach(i -> i.accept(requestBuilder));

            assertThat(requestBuilder.build().timeout()).isPresent().contains(Duration.ofSeconds(10));
        }
    }


    @Nested
    class NoPropertiesSet {

        @Autowired
        private ShapeshifterProperties properties;

        @Test
        void shouldNotConfigureConnectTimeoutByDefault() throws Exception {
            var service = new ShapeshifterConfiguration(properties).uftpSendMessageService(
                    uftpSerializer, uftpCryptoService, participantResolutionService,
                    participantAuthorizationProvider, uftpValidationService);

            var httpClientField = UftpSendMessageService.class.getDeclaredField("httpClient");
            httpClientField.setAccessible(true);
            var httpClient = (HttpClient) httpClientField.get(service);

            assertThat(httpClient.connectTimeout()).isEmpty();
        }

        @Test
        void shouldNotRegisterReadTimeoutInterceptorByDefault() throws Exception {
            var service = new ShapeshifterConfiguration(properties).uftpSendMessageService(
                    uftpSerializer, uftpCryptoService, participantResolutionService,
                    participantAuthorizationProvider, uftpValidationService);

            var interceptorsField = UftpSendMessageService.class.getDeclaredField("requestInterceptors");
            interceptorsField.setAccessible(true);
            var interceptors = (List<?>) interceptorsField.get(service);

            assertThat(interceptors).isEmpty();
        }
    }

}