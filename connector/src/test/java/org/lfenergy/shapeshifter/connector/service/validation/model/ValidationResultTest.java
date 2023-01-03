package org.lfenergy.shapeshifter.connector.service.validation.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidationResultTest {

  @Test
  void ok() {
    var result = ValidationResult.ok();

    assertThat(result.valid()).isTrue();
    assertThat(result.rejectionReason()).isNull();
  }

  @Test
  void rejection() {
    var result = ValidationResult.rejection("reason");

    assertThat(result.valid()).isFalse();
    assertThat(result.rejectionReason()).isEqualTo("reason");
  }

  @Test
  void rejection_throwWhenNull() {
    var thrown = assertThrows(Exception.class, () ->
        ValidationResult.rejection(null));

    assertThat(thrown).hasMessage("Rejection reason cannot be blank.")
                      .isInstanceOf(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", " ", "\t", "\n", "\r", "\f", " \t\n\r\f"})
  void rejection_throwWhenBlank(String rejectionReason) {
    var thrown = assertThrows(Exception.class, () ->
        ValidationResult.rejection(rejectionReason));

    assertThat(thrown).hasMessage("Rejection reason cannot be blank.")
                      .isInstanceOf(IllegalArgumentException.class);
  }
}