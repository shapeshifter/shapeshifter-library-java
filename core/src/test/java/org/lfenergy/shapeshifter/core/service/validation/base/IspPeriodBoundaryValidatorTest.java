// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IspPeriodBoundaryValidatorTest {

  @InjectMocks
  private IspPeriodBoundaryValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("ISPs out of bounds");
  }

  @ParameterizedTest
  @ValueSource(longs = {0, -1, -2, -10})
  void validateIsps_failsWhenStartZeroOrSmaller(long start) {
    assertThat(testSubject.validateIsps(96, List.of(new IspInfo(start)))).isFalse();
  }

  @ParameterizedTest
  @ValueSource(longs = {0, -1, -2, -10})
  void validateIsps_failsWhenDurationZeroOrSmaller(long duration) {
    assertThat(testSubject.validateIsps(96, List.of(new IspInfo(1, duration)))).isFalse();
  }

  @ParameterizedTest
  @ValueSource(longs = {97, 100})
  void validateIsps_failsWhenEndBeyondMaxAllowed(long start) {
    assertThat(testSubject.validateIsps(96, List.of(new IspInfo(start, 1)))).isFalse();
  }

  @Test
  void validateIsps_failsWhenOneIsIncorrect() {
    assertThat(testSubject.validateIsps(96, List.of(
        new IspInfo(1, 1),
        new IspInfo(2, 1),
        new IspInfo(3, 1),
        new IspInfo(4, 1),
        new IspInfo(5, 1),
        new IspInfo(-6, 1)
    ))).isFalse();
  }

  @Test
  void validateIsps_successWhenAllCorrect() {
    assertThat(testSubject.validateIsps(96, List.of(
        new IspInfo(1, 1),
        new IspInfo(2, 1),
        new IspInfo(3, 1),
        new IspInfo(4, 1),
        new IspInfo(5, 90),
        new IspInfo(95, 1),
        new IspInfo(96, 1)
    ))).isTrue();
  }
}