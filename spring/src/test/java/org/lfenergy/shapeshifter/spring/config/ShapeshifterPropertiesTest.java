package org.lfenergy.shapeshifter.spring.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ShapeshifterPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class);

    @EnableConfigurationProperties(ShapeshifterProperties.class)
    static class TestConfig {
    }

    @Test
    void shouldBindHttpProperties() {
        contextRunner.withPropertyValues(
                "shapeshifter.http.connection-timeout=5s",
                "shapeshifter.http.read-timeout=10s"
        ).run(context -> {
            assertThat(context).hasSingleBean(ShapeshifterProperties.class);
            ShapeshifterProperties properties = context.getBean(ShapeshifterProperties.class);
            assertThat(properties.http()).isNotNull();
            assertThat(properties.http().connectionTimeout()).isEqualTo(Duration.ofSeconds(5));
            assertThat(properties.http().readTimeout()).isEqualTo(Duration.ofSeconds(10));
        });
    }

    @Test
    void shouldHandleMissingHttpProperties() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ShapeshifterProperties.class);
            ShapeshifterProperties properties = context.getBean(ShapeshifterProperties.class);
            assertThat(properties.http()).isNull();
        });
    }
}
