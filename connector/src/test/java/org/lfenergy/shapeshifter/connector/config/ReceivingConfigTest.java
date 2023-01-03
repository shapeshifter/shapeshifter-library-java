package org.lfenergy.shapeshifter.connector.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReceivingConfigTest {

  @Test
  void construction_null() {
    var config = new ReceivingConfig(null);

    assertThat(config.validation()).isNotNull();
  }

  @Test
  void construction_instance() {
    var validation = new ValidationConfig(null);
    var config = new ReceivingConfig(validation);

    assertThat(config.validation()).isSameAs(validation);
  }

}