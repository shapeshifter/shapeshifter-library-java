// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class DateTimeCompareAllowingInfiniteTest {

  private static final OffsetDateTime DATETIME = OffsetDateTime.parse("2022-11-22T00:00:00+01:00");
  private static final OffsetDateTime INFINITE = null;

  @Test
  void equalOrAfter_true_forSameDate() {
    assertThat(DateTimeCompareAllowingInfinite.equalOrAfter(DATETIME, DATETIME)).isTrue();
  }

  @Test
  void equalOrAfter_true_forAfter() {
    assertThat(DateTimeCompareAllowingInfinite.equalOrAfter(DATETIME.plusMinutes(1), DATETIME)).isTrue();
  }

  @Test
  void equalOrAfter_false_forBefore() {
    assertThat(DateTimeCompareAllowingInfinite.equalOrAfter(DATETIME.minusMinutes(1), DATETIME)).isFalse();
  }

  @Test
  void equalOrAfter_true_bothInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.equalOrAfter(INFINITE, INFINITE)).isTrue();
  }

  @Test
  void equalOrAfter_true_firstInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.equalOrAfter(INFINITE, DATETIME)).isTrue();
  }

  @Test
  void equalOrAfter_false_compareToIsInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.equalOrAfter(DATETIME, INFINITE)).isFalse();
  }

  @Test
  void isEqual_sameDate() {
    assertThat(DateTimeCompareAllowingInfinite.isEqual(DATETIME, DATETIME)).isTrue();
  }

  @Test
  void isEqual_bothInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.isEqual(INFINITE, INFINITE)).isTrue();
  }

  @Test
  void isEqual_false_notSameDate_after() {
    assertThat(DateTimeCompareAllowingInfinite.isEqual(DATETIME.plusMinutes(1), DATETIME)).isFalse();
  }

  @Test
  void isEqual_false_notSameDate_before() {
    assertThat(DateTimeCompareAllowingInfinite.isEqual(DATETIME.minusMinutes(1), DATETIME)).isFalse();
  }

  @Test
  void isEqual_false_firstInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.isEqual(INFINITE, DATETIME)).isFalse();
  }

  @Test
  void isEqual_false_secondInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.isEqual(DATETIME, INFINITE)).isFalse();
  }

  @Test
  void isAfter_false_sameDate() {
    assertThat(DateTimeCompareAllowingInfinite.isAfter(DATETIME, DATETIME)).isFalse();
  }

  @Test
  void isAfter_false_bothInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.isAfter(INFINITE, INFINITE)).isFalse();
  }

  @Test
  void isAfter_false_whenBefore() {
    assertThat(DateTimeCompareAllowingInfinite.isAfter(DATETIME.minusMinutes(1), DATETIME)).isFalse();
  }

  @Test
  void isAfter_false_whenCompareToInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.isAfter(DATETIME, INFINITE)).isFalse();
  }

  @Test
  void isAfter_true_whenLarger() {
    assertThat(DateTimeCompareAllowingInfinite.isAfter(DATETIME.plusMinutes(1), DATETIME)).isTrue();
  }

  @Test
  void isAfter_true_whenInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.isAfter(INFINITE, DATETIME)).isTrue();
  }

  @Test
  void isInfinite_true_whenInfinite() {
    assertThat(DateTimeCompareAllowingInfinite.isInfinite(INFINITE)).isTrue();
  }

  @Test
  void isInfinite_false_whenDateTime() {
    assertThat(DateTimeCompareAllowingInfinite.isInfinite(DATETIME)).isFalse();
  }
}