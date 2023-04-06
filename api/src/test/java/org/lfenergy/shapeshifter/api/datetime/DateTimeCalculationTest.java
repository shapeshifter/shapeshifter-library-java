// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.datetime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDate;
import java.time.zone.ZoneRulesException;
import java.util.stream.Stream;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class DateTimeCalculationTest {

  private static final long SECS_PER_MIN = 60;
  private static final long SECS_PER_HOUR = 60 * SECS_PER_MIN;
  private static final long SECS_PER_DAY = 24 * SECS_PER_HOUR;

  private static final LocalDate WINTER_TIME_DAY = LocalDate.parse("2022-11-22");
  private static final LocalDate SUMMER_TIME_DAY = LocalDate.parse("2022-06-22");
  // EU: Last Sunday of March (March 27th for 2022)
  private static final LocalDate WINTER_TO_SUMMER_TIME_TRANSITION_DAY = LocalDate.parse("2022-03-27");
  // EU: Last Sunday of October (October 10th for 2022)
  private static final LocalDate SUMMER_TO_WINTER_TIME_TRANSITION_DAY = LocalDate.parse("2022-10-30");
  private static final javax.xml.datatype.Duration DURATION_15_MINUTES;

  static {
    try {
      DURATION_15_MINUTES = DatatypeFactory.newInstance().newDuration("PT15M");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private javax.xml.datatype.Duration createXmlDuration(int days, int hours, int minutes, int seconds) {
    return createXmlDuration(0, 0, days, hours, minutes, seconds);
  }

  private javax.xml.datatype.Duration createXmlDuration(int years, int months, int days, int hours, int minutes, int seconds) {
    try {
      String xmlString = "P"
          + (years > 0 ? years + "Y" : "")
          + (months > 0 ? months + "M" : "")
          + (days > 0 ? days + "D" : "")
          + (hours > 0 || minutes > 0 || seconds > 0 ? "T" : "")
          + (hours > 0 ? hours + "H" : "")
          + (minutes > 0 ? minutes + "M" : "")
          + (seconds > 0 ? seconds + "S" : "");
      if (xmlString.equals("P")) {
        xmlString = "PT0S"; // 0 seconds.
      }
      return DatatypeFactory.newInstance().newDuration(xmlString);
    } catch (DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static Stream<Arguments> durationsWithoutMonthsOrYears() {
    return Stream.of(
        Arguments.of(0, 0, 0, 0),
        Arguments.of(0, 0, 0, 1),
        Arguments.of(0, 0, 1, 2),
        Arguments.of(0, 1, 2, 3),
        Arguments.of(1, 2, 3, 4),
        Arguments.of(12, 23, 56, 35)
    );
  }

  public static Stream<Arguments> durationsWithMonthsOrYears() {
    return Stream.of(
        Arguments.of(0, 1, 0, 0, 0, 0),
        Arguments.of(1, 0, 0, 0, 0, 0),
        Arguments.of(1, 1, 0, 0, 0, 0)
    );
  }

  @ParameterizedTest
  @MethodSource("durationsWithoutMonthsOrYears")
  void toJavaTime(int days, int hours, int minutes, int seconds) {
    javax.xml.datatype.Duration xmlValue = createXmlDuration(days, hours, minutes, seconds);

    var actual = DateTimeCalculation.toJavaTime(xmlValue);

    assertThat(actual.getSeconds()).isEqualTo(
        days * SECS_PER_DAY + hours * SECS_PER_HOUR + minutes * SECS_PER_MIN + seconds);
    assertThat(actual.getNano()).isZero();
  }

  @ParameterizedTest
  @MethodSource("durationsWithMonthsOrYears")
  void toJavaTime(int years, int months, int days, int hours, int minutes, int seconds) {
    javax.xml.datatype.Duration xmlValue = createXmlDuration(years, months, days, hours, minutes, seconds);

    var thrown = assertThrows(IllegalArgumentException.class, () ->
        DateTimeCalculation.toJavaTime(xmlValue));

    assertThat(thrown.getMessage()).isEqualTo("Duration cannot be converted without offset because it contains months and/or years.");
  }

  @ParameterizedTest
  @MethodSource("durationsWithoutMonthsOrYears")
  void canBeConvertedWithoutOffset_true_withOnlyDaysHoursMinutesAndOrSeconds(int days, int hours, int minutes, int seconds) {
    javax.xml.datatype.Duration xmlValue = createXmlDuration(days, hours, minutes, seconds);

    assertThat(DateTimeCalculation.canBeConvertedWithoutOffset(xmlValue)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("durationsWithMonthsOrYears")
  void canBeConvertedWithoutOffset_false_whenYearAndOrMonthIsSet(int years, int months, int days, int hours, int minutes, int seconds) {
    javax.xml.datatype.Duration xmlValue = createXmlDuration(years, months, days, hours, minutes, seconds);

    assertThat(DateTimeCalculation.canBeConvertedWithoutOffset(xmlValue)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"Europe/Amsterdam", "Europe/London", "Europe/Paris"})
  void ianaTimeZone_to_toZoneId(String ianaTimeZone) {
    var result = DateTimeCalculation.toZoneId(ianaTimeZone);

    assertThat(result).isNotNull();
    assertThat(result).hasToString(ianaTimeZone);
  }

  @Test
  void invalid_ianaTimeZone_to_toZoneId() {
    ZoneRulesException thrown = assertThrows(ZoneRulesException.class, () ->
        DateTimeCalculation.toZoneId("boe"));

    assertThat(thrown.getMessage()).isEqualTo("Unknown time-zone ID: boe");
  }

  public static Stream<Arguments> for_toZonedDateTime() {
    return Stream.of(
        Arguments.of(WINTER_TIME_DAY, "Europe/Amsterdam", "2022-11-22T00:00+01:00[Europe/Amsterdam]"),
        Arguments.of(SUMMER_TIME_DAY, "Europe/Amsterdam", "2022-06-22T00:00+02:00[Europe/Amsterdam]"),
        Arguments.of(WINTER_TO_SUMMER_TIME_TRANSITION_DAY, "Europe/Amsterdam", "2022-03-27T00:00+01:00[Europe/Amsterdam]"),
        Arguments.of(SUMMER_TO_WINTER_TIME_TRANSITION_DAY, "Europe/Amsterdam", "2022-10-30T00:00+02:00[Europe/Amsterdam]"),
        Arguments.of(WINTER_TIME_DAY, "Europe/London", "2022-11-22T00:00Z[Europe/London]"),
        Arguments.of(SUMMER_TIME_DAY, "Europe/London", "2022-06-22T00:00+01:00[Europe/London]"),
        Arguments.of(WINTER_TO_SUMMER_TIME_TRANSITION_DAY, "Europe/London", "2022-03-27T00:00Z[Europe/London]"),
        Arguments.of(SUMMER_TO_WINTER_TIME_TRANSITION_DAY, "Europe/London", "2022-10-30T00:00+01:00[Europe/London]"),
        Arguments.of(WINTER_TIME_DAY, "Europe/Paris", "2022-11-22T00:00+01:00[Europe/Paris]"),
        Arguments.of(SUMMER_TIME_DAY, "Europe/Paris", "2022-06-22T00:00+02:00[Europe/Paris]"),
        Arguments.of(WINTER_TO_SUMMER_TIME_TRANSITION_DAY, "Europe/Paris", "2022-03-27T00:00+01:00[Europe/Paris]"),
        Arguments.of(SUMMER_TO_WINTER_TIME_TRANSITION_DAY, "Europe/Paris", "2022-10-30T00:00+02:00[Europe/Paris]")
    );
  }

  @ParameterizedTest
  @MethodSource("for_toZonedDateTime")
  void pointInTime_and_ianaTimeZoneName_toZonedDateTime(LocalDate pointInTime, String ianaTimeZone, String expectedResultToString) {
    var result = DateTimeCalculation.toZonedDateTime(pointInTime, ianaTimeZone);

    assertThat(result).isNotNull();
    assertThat(result).hasToString(expectedResultToString);
  }

  public static Stream<Arguments> for_lengthOfDay() {
    return Stream.of(
        Arguments.of(WINTER_TIME_DAY, "Europe/Amsterdam", 24 * SECS_PER_HOUR),
        Arguments.of(SUMMER_TIME_DAY, "Europe/Amsterdam", 24 * SECS_PER_HOUR),
        Arguments.of(WINTER_TO_SUMMER_TIME_TRANSITION_DAY, "Europe/Amsterdam", 23 * SECS_PER_HOUR),
        Arguments.of(SUMMER_TO_WINTER_TIME_TRANSITION_DAY, "Europe/Amsterdam", 25 * SECS_PER_HOUR),
        Arguments.of(WINTER_TIME_DAY, "Europe/London", 24 * SECS_PER_HOUR),
        Arguments.of(SUMMER_TIME_DAY, "Europe/London", 24 * SECS_PER_HOUR),
        Arguments.of(WINTER_TO_SUMMER_TIME_TRANSITION_DAY, "Europe/London", 23 * SECS_PER_HOUR),
        Arguments.of(SUMMER_TO_WINTER_TIME_TRANSITION_DAY, "Europe/London", 25 * SECS_PER_HOUR),
        Arguments.of(WINTER_TIME_DAY, "Europe/Paris", 24 * SECS_PER_HOUR),
        Arguments.of(SUMMER_TIME_DAY, "Europe/Paris", 24 * SECS_PER_HOUR),
        Arguments.of(WINTER_TO_SUMMER_TIME_TRANSITION_DAY, "Europe/Paris", 23 * SECS_PER_HOUR),
        Arguments.of(SUMMER_TO_WINTER_TIME_TRANSITION_DAY, "Europe/Paris", 25 * SECS_PER_HOUR)
    );
  }

  @ParameterizedTest
  @MethodSource("for_lengthOfDay")
  void lengthOfDayInTimeZone(LocalDate onDay, String ianaTimeZone, long expectedDurationSeconds) {
    var result = DateTimeCalculation.lengthOfDay(onDay, ianaTimeZone);

    assertThat(result.getSeconds()).isEqualTo(expectedDurationSeconds);
    assertThat(result.getNano()).isZero();
  }

  public static Stream<Arguments> for_startOfDay() {
    return Stream.of(
        Arguments.of(LocalDate.parse("2022-11-22"), "2022-11-22"),
        Arguments.of(LocalDate.parse("2022-11-22"), "2022-11-22"),
        Arguments.of(LocalDate.parse("2022-11-22"), "2022-11-22"),
        Arguments.of(LocalDate.parse("2022-06-22"), "2022-06-22"),
        Arguments.of(LocalDate.parse("2022-06-22"), "2022-06-22"),
        Arguments.of(LocalDate.parse("2022-06-22"), "2022-06-22"),
        Arguments.of(LocalDate.parse("2022-03-27"), "2022-03-27"),
        Arguments.of(LocalDate.parse("2022-03-27"), "2022-03-27"),
        Arguments.of(LocalDate.parse("2022-03-27"), "2022-03-27"),
        Arguments.of(LocalDate.parse("2022-10-30"), "2022-10-30"),
        Arguments.of(LocalDate.parse("2022-10-30"), "2022-10-30"),
        Arguments.of(LocalDate.parse("2022-10-30"), "2022-10-30"),
        Arguments.of(LocalDate.parse("2022-10-30"), "2022-10-30")
    );
  }

  @ParameterizedTest
  @MethodSource("for_startOfDay")
  void startOfDay_forOffsetDateTime(LocalDate pointInTime, String expectedStartOfDayAsString) {
    assertThat(pointInTime).hasToString(expectedStartOfDayAsString);
  }


  public static Stream<Arguments> for_ispEndInDay_differentIspDurations() {
    return Stream.of(
        Arguments.of(LocalDate.parse("2022-11-22"), 1, 10, "2022-11-22T00:10+01:00"),
        Arguments.of(LocalDate.parse("2022-11-22"), 1, 15, "2022-11-22T00:15+01:00"),
        Arguments.of(LocalDate.parse("2022-11-22"), 1, 20, "2022-11-22T00:20+01:00"),
        Arguments.of(LocalDate.parse("2022-06-22"), 1, 30, "2022-06-22T00:30+02:00"),
        Arguments.of(LocalDate.parse("2022-06-22"), 1, 60, "2022-06-22T01:00+02:00"),
        Arguments.of(LocalDate.parse("2022-06-22"), 1, 90, "2022-06-22T01:30+02:00")
    );
  }

  @ParameterizedTest
  @MethodSource("for_ispEndInDay_differentIspDurations")
  void ispEndInDay_differentIspDurations(LocalDate onDay, long ofOneBasedIspIndex, long minutesIspDuration, String expectedEndTimeAsString) {
    var ispDuration = Duration.ofMinutes(minutesIspDuration);

    var result = DateTimeCalculation.ispEndInDay(onDay, "Europe/Amsterdam", ofOneBasedIspIndex, ispDuration);

    assertThat(result).hasToString(expectedEndTimeAsString);
  }

  public static Stream<Arguments> for_ispEndInDay() {
    return Stream.of(
        Arguments.of(LocalDate.parse("2022-11-22"), 4, 15, "2022-11-22T01:00+01:00"), // 1
        Arguments.of(LocalDate.parse("2022-11-22"), 7, 15, "2022-11-22T01:45+01:00"), // 2
        Arguments.of(LocalDate.parse("2022-11-22"), 8, 15, "2022-11-22T02:00+01:00"), // 3
        Arguments.of(LocalDate.parse("2022-11-22"), 9, 15, "2022-11-22T02:15+01:00"), // 4
        Arguments.of(LocalDate.parse("2022-11-22"), 10, 15, "2022-11-22T02:30+01:00"), // 5
        Arguments.of(LocalDate.parse("2022-11-22"), 11, 15, "2022-11-22T02:45+01:00"), // 6
        Arguments.of(LocalDate.parse("2022-11-22"), 12, 15, "2022-11-22T03:00+01:00"), // 7
        Arguments.of(LocalDate.parse("2022-11-22"), 13, 15, "2022-11-22T03:15+01:00"), // 8
        Arguments.of(LocalDate.parse("2022-11-22"), 40, 15, "2022-11-22T10:00+01:00"), // 9
        Arguments.of(LocalDate.parse("2022-11-22"), 60, 15, "2022-11-22T15:00+01:00"), // 10
        Arguments.of(LocalDate.parse("2022-06-22"), 4, 15, "2022-06-22T01:00+02:00"), // 11
        Arguments.of(LocalDate.parse("2022-06-22"), 7, 15, "2022-06-22T01:45+02:00"), // 12
        Arguments.of(LocalDate.parse("2022-06-22"), 8, 15, "2022-06-22T02:00+02:00"), // 13
        Arguments.of(LocalDate.parse("2022-06-22"), 9, 15, "2022-06-22T02:15+02:00"), // 14
        Arguments.of(LocalDate.parse("2022-06-22"), 10, 15, "2022-06-22T02:30+02:00"), // 15
        Arguments.of(LocalDate.parse("2022-06-22"), 11, 15, "2022-06-22T02:45+02:00"), // 16
        Arguments.of(LocalDate.parse("2022-06-22"), 12, 15, "2022-06-22T03:00+02:00"), // 17
        Arguments.of(LocalDate.parse("2022-06-22"), 13, 15, "2022-06-22T03:15+02:00"), // 18
        Arguments.of(LocalDate.parse("2022-06-22"), 40, 15, "2022-06-22T10:00+02:00"), // 19
        Arguments.of(LocalDate.parse("2022-06-22"), 60, 15, "2022-06-22T15:00+02:00"), // 20
        Arguments.of(LocalDate.parse("2022-03-27"), 4, 15, "2022-03-27T01:00+01:00"), // 21
        Arguments.of(LocalDate.parse("2022-03-27"), 7, 15, "2022-03-27T01:45+01:00"), // 22
        Arguments.of(LocalDate.parse("2022-03-27"), 8, 15, "2022-03-27T03:00+02:00"), // 23
        Arguments.of(LocalDate.parse("2022-03-27"), 9, 15, "2022-03-27T03:15+02:00"), // 24
        Arguments.of(LocalDate.parse("2022-03-27"), 10, 15, "2022-03-27T03:30+02:00"), // 25
        Arguments.of(LocalDate.parse("2022-03-27"), 11, 15, "2022-03-27T03:45+02:00"), // 26
        Arguments.of(LocalDate.parse("2022-03-27"), 12, 15, "2022-03-27T04:00+02:00"), // 27
        Arguments.of(LocalDate.parse("2022-03-27"), 13, 15, "2022-03-27T04:15+02:00"), // 28
        Arguments.of(LocalDate.parse("2022-03-27"), 40, 15, "2022-03-27T11:00+02:00"), // 29
        Arguments.of(LocalDate.parse("2022-03-27"), 60, 15, "2022-03-27T16:00+02:00"), // 30
        Arguments.of(LocalDate.parse("2022-10-30"), 4, 15, "2022-10-30T01:00+02:00"), // 31
        Arguments.of(LocalDate.parse("2022-10-30"), 7, 15, "2022-10-30T01:45+02:00"), // 32
        Arguments.of(LocalDate.parse("2022-10-30"), 8, 15, "2022-10-30T02:00+02:00"), // 33
        Arguments.of(LocalDate.parse("2022-10-30"), 9, 15, "2022-10-30T02:15+02:00"), // 34
        Arguments.of(LocalDate.parse("2022-10-30"), 10, 15, "2022-10-30T02:30+02:00"), // 35
        Arguments.of(LocalDate.parse("2022-10-30"), 11, 15, "2022-10-30T02:45+02:00"), // 36
        Arguments.of(LocalDate.parse("2022-10-30"), 12, 15, "2022-10-30T02:00+01:00"), // 37
        Arguments.of(LocalDate.parse("2022-10-30"), 13, 15, "2022-10-30T02:15+01:00"), // 38
        Arguments.of(LocalDate.parse("2022-10-30"), 40, 15, "2022-10-30T09:00+01:00"), // 39
        Arguments.of(LocalDate.parse("2022-10-30"), 60, 15, "2022-10-30T14:00+01:00") // 40
    );
  }

  @ParameterizedTest
  @MethodSource("for_ispEndInDay")
  void ispEndInDay(LocalDate onDay, long ofOneBasedIspIndex, long minutesIspDuration, String expectedEndTimeAsString) {
    var ispDuration = Duration.ofMinutes(minutesIspDuration);

    var result = DateTimeCalculation.ispEndInDay(onDay, "Europe/Amsterdam", ofOneBasedIspIndex, ispDuration);

    assertThat(result).hasToString(expectedEndTimeAsString);
  }

  @Test
  void ispEndInDay_moreThanFitInOneDay() {
    var ispDuration = Duration.ofMinutes(15);
    var onDay = SUMMER_TIME_DAY; // 22nd of june
    long ofOneBasedIspIndex = 200; // 50 hours = 2 days and 2 hours

    var result = DateTimeCalculation.ispEndInDay(onDay, "Europe/Amsterdam", ofOneBasedIspIndex, ispDuration);

    assertThat(result).hasToString("2022-06-24T02:00+02:00");
  }
}