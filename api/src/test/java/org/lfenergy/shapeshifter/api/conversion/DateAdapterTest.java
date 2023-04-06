// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.conversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

class DateAdapterTest {

  @Test
  void parseNull() {
    LocalDate parsed = DateAdapter.parse(null);
    assertThat(parsed).isNull();

    String printed = DateAdapter.print(null);
    assertThat(printed).isNull();
  }

  @Test
  void parse() {
    LocalDate parsed = DateAdapter.parse("2020-04-22");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22");
  }

  @Test
  void parseZ() {
    LocalDate parsed = DateAdapter.parse("2020-04-22Z");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22");
  }

  @Test
  void parseMinusHours() {
    LocalDate parsed = DateAdapter.parse("2020-04-22-02:00");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22");
  }

  @Test
  void parsePlusHours() {
    LocalDate parsed = DateAdapter.parse("2020-04-22+02:00");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22");
  }

}