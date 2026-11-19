// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
  void of_valid_returnsOkAndIgnoresReason() {
    assertThat(ValidationResult.of(true, null)).isEqualTo(ValidationResult.ok());
  }

  @Test
  void of_invalid_returnsRejection() {
    assertThat(ValidationResult.of(false, "reason")).isEqualTo(ValidationResult.rejection("reason"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\t"})
  void of_invalidWithBlankReason_throws(String rejectionReason) {
    assertThrows(IllegalArgumentException.class, () -> ValidationResult.of(false, rejectionReason));
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