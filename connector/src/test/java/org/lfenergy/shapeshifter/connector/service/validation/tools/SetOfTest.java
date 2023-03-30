// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SetOfTest {

  @Test
  void setOfNullable_null() {
    String value = null;
    Set<String> actual = SetOf.setOfNullable(value);
    assertThat(actual).isEmpty();
  }

  @Test
  void setOfNullable_value() {
    String value = "boe";
    Set<String> actual = SetOf.setOfNullable(value);
    assertThat(actual).containsExactly("boe");
  }

  @Test
  void setOfOptional_empty() {
    Optional<String> value = Optional.empty();
    Set<String> actual = SetOf.setOfOptional(value);
    assertThat(actual).isEmpty();
  }

  @Test
  void setOfOptional_value() {
    Optional<String> value = Optional.of("boe");
    Set<String> actual = SetOf.setOfOptional(value);
    assertThat(actual).containsExactly("boe");
  }
}