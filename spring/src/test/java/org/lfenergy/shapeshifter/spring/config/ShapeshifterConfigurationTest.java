package org.lfenergy.shapeshifter.spring.config;

import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.core.service.ParticipantAuthorizationProvider;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.core.service.sending.UftpSendMessageService;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ShapeshifterConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ShapeshifterConfiguration.class))
            .withUserConfiguration(MockConfig.class)
            .withPropertyValues("spring.main.web-application-type=servlet");

    @Configuration
    @EnableConfigurationProperties(ShapeshifterProperties.class)
    static class MockConfig {
        @Bean UftpSerializer uftpSerializer() { return mock(UftpSerializer.class); }
        @Bean UftpCryptoService uftpCryptoService() { return mock(UftpCryptoService.class); }
        @Bean ParticipantResolutionService participantResolutionService() { return mock(ParticipantResolutionService.class); }
        @Bean ParticipantAuthorizationProvider participantAuthorizationProvider() { return mock(ParticipantAuthorizationProvider.class); }
        @Bean UftpValidationService uftpValidationService() { return mock(UftpValidationService.class); }
    }

    @Test
    void shouldConfigureTimeouts() {
        contextRunner.withPropertyValues(
                "shapeshifter.http.connection-timeout=5s",
                "shapeshifter.http.read-timeout=10s"
        ).run(context -> {
            assertThat(context).hasSingleBean(ShapeshifterProperties.class);
            ShapeshifterProperties properties = context.getBean(ShapeshifterProperties.class);
            assertThat(properties.http()).isNotNull();
            assertThat(properties.http().connectionTimeout()).isEqualTo(Duration.ofSeconds(5));
            assertThat(properties.http().readTimeout()).isEqualTo(Duration.ofSeconds(10));
            
            UftpSendMessageService service = new ShapeshifterConfiguration(properties).uftpSendMessageService(
                    context.getBean(UftpSerializer.class),
                    context.getBean(UftpCryptoService.class),
                    context.getBean(ParticipantResolutionService.class),
                    context.getBean(ParticipantAuthorizationProvider.class),
                    context.getBean(UftpValidationService.class)
            );
            
            var readTimeoutField = UftpSendMessageService.class.getDeclaredField("readTimeout");
            readTimeoutField.setAccessible(true);
            assertThat(readTimeoutField.get(service)).isEqualTo(Duration.ofSeconds(10));

            var httpClientMethod = ShapeshifterConfiguration.class.getDeclaredMethod("httpClient");
            httpClientMethod.setAccessible(true);
            HttpClient httpClient = (HttpClient) httpClientMethod.invoke(new ShapeshifterConfiguration(properties));
            assertThat(httpClient.connectTimeout()).isPresent().contains(Duration.ofSeconds(5));
        });
    }

    @Test
    void shouldConfigureDefaultTimeouts() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ShapeshifterProperties.class);
            ShapeshifterProperties properties = context.getBean(ShapeshifterProperties.class);

            UftpSendMessageService service = new ShapeshifterConfiguration(properties).uftpSendMessageService(
                    context.getBean(UftpSerializer.class),
                    context.getBean(UftpCryptoService.class),
                    context.getBean(ParticipantResolutionService.class),
                    context.getBean(ParticipantAuthorizationProvider.class),
                    context.getBean(UftpValidationService.class)
            );
            
            var readTimeoutField = UftpSendMessageService.class.getDeclaredField("readTimeout");
            readTimeoutField.setAccessible(true);
            assertThat(readTimeoutField.get(service)).isEqualTo(Duration.ofSeconds(3600));

            var httpClientMethod = ShapeshifterConfiguration.class.getDeclaredMethod("httpClient");
            httpClientMethod.setAccessible(true);
            HttpClient httpClient = (HttpClient) httpClientMethod.invoke(new ShapeshifterConfiguration(properties));
            assertThat(httpClient.connectTimeout()).isEmpty();
        });
    }
}
