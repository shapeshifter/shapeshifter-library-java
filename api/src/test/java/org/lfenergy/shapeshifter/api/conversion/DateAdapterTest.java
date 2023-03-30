// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.conversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class DateAdapterTest {

  @Test
  public void parseNull() {
    OffsetDateTime parsed = DateAdapter.parse(null);
    assertThat(parsed).isNull();

    String printed = DateAdapter.print(null);
    assertThat(printed).isNull();
  }

  @Test
  public void parse() {
    OffsetDateTime parsed = DateAdapter.parse("2020-04-22");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isZero();
    assertThat(parsed.getMinute()).isZero();
    assertThat(parsed.getSecond()).isZero();
    assertThat(parsed.getNano()).isZero();
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.UTC);

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22Z");
  }

  @Test
  public void parseZ() {
    OffsetDateTime parsed = DateAdapter.parse("2020-04-22Z");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isZero();
    assertThat(parsed.getMinute()).isZero();
    assertThat(parsed.getSecond()).isZero();
    assertThat(parsed.getNano()).isZero();
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.UTC);

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22Z");
  }

  @Test
  public void parseMinusHours() {
    OffsetDateTime parsed = DateAdapter.parse("2020-04-22-06:00");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isZero();
    assertThat(parsed.getMinute()).isZero();
    assertThat(parsed.getSecond()).isZero();
    assertThat(parsed.getNano()).isZero();
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.ofHours(-6));

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22-06:00");
  }

  @Test
  public void parseMinusHoursAndMinutes() {
    OffsetDateTime parsed = DateAdapter.parse("2020-04-22-09:30");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isZero();
    assertThat(parsed.getMinute()).isZero();
    assertThat(parsed.getSecond()).isZero();
    assertThat(parsed.getNano()).isZero();
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.ofHoursMinutes(-9, -30));

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22-09:30");
  }

  @Test
  public void parsePlusHours() {
    OffsetDateTime parsed = DateAdapter.parse("2020-04-22+02:00");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isZero();
    assertThat(parsed.getMinute()).isZero();
    assertThat(parsed.getSecond()).isZero();
    assertThat(parsed.getNano()).isZero();
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.ofHours(2));

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22+02:00");
  }

  @Test
  public void parsePlusHoursAndMinutes() {
    OffsetDateTime parsed = DateAdapter.parse("2020-04-22+04:15");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isZero();
    assertThat(parsed.getMinute()).isZero();
    assertThat(parsed.getSecond()).isZero();
    assertThat(parsed.getNano()).isZero();
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.ofHoursMinutes(4, 15));

    String printed = DateAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22+04:15");
  }
}