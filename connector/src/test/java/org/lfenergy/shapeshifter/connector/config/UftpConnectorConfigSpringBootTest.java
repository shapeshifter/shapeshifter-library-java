package org.lfenergy.shapeshifter.connector.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.connector.application.TestSpringConfigExcludingTestMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestSpringConfigExcludingTestMappings.class)
class UftpConnectorConfigSpringBootTest {

  @Autowired
  private UftpConnectorConfig config;

  @Test
  void test() {
    assertThat(config.receiving().validation().enabled()).isFalse();
  }

}