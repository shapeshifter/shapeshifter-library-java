package org.lfenergy.shapeshifter.connector.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ValidationConfigTest {

  @Test
  void construction_null() {
    var config = new ValidationConfig(null);

    assertThat(config.enabled()).isTrue();
  }

  @Test
  void construction_true() {
    var config = new ValidationConfig(true);

    assertThat(config.enabled()).isTrue();
  }

  @Test
  void construction_false() {
    var config = new ValidationConfig(false);

    assertThat(config.enabled()).isFalse();
  }
}