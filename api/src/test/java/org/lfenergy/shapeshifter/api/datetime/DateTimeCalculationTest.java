// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.datetime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.OffsetDateTime;
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

  private static final OffsetDateTime WINTER_TIME_DAY = OffsetDateTime.parse("2022-11-22T00:00:00+01:00");
  private static final OffsetDateTime SUMMER_TIME_DAY = OffsetDateTime.parse("2022-06-22T00:00:00+02:00");
  // EU: Last Sunday of March (March 27th for 2022)
  private static final OffsetDateTime WINTER_TO_SUMMER_TIME_TRANSITION_DAY = OffsetDateTime.parse("2022-03-27T00:00:00+01:00");
  // EU: Last Sunday of October (October 10th for 2022)
  private static final OffsetDateTime SUMMER_TO_WINTER_TIME_TRANSITION_DAY = OffsetDateTime.parse("2022-10-30T00:00:00+02:00");
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
        Arguments.of(WINTER_TIME_DAY, "Europe/London", "2022-11-21T23:00Z[Europe/London]"),
        Arguments.of(SUMMER_TIME_DAY, "Europe/London", "2022-06-21T23:00+01:00[Europe/London]"),
        Arguments.of(WINTER_TO_SUMMER_TIME_TRANSITION_DAY, "Europe/London", "2022-03-26T23:00Z[Europe/London]"),
        Arguments.of(SUMMER_TO_WINTER_TIME_TRANSITION_DAY, "Europe/London", "2022-10-29T23:00+01:00[Europe/London]"),
        Arguments.of(WINTER_TIME_DAY, "Europe/Paris", "2022-11-22T00:00+01:00[Europe/Paris]"),
        Arguments.of(SUMMER_TIME_DAY, "Europe/Paris", "2022-06-22T00:00+02:00[Europe/Paris]"),
        Arguments.of(WINTER_TO_SUMMER_TIME_TRANSITION_DAY, "Europe/Paris", "2022-03-27T00:00+01:00[Europe/Paris]"),
        Arguments.of(SUMMER_TO_WINTER_TIME_TRANSITION_DAY, "Europe/Paris", "2022-10-30T00:00+02:00[Europe/Paris]")
    );
  }

  @ParameterizedTest
  @MethodSource("for_toZonedDateTime")
  void pointInTime_and_ianaTimeZoneName_toZonedDateTime(OffsetDateTime pointInTime, String ianaTimeZone, String expectedResultToString) {
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
  void lengthOfDayInTimeZone(OffsetDateTime onDay, String ianaTimeZone, long expectedDurationSeconds) {
    var result = DateTimeCalculation.lengthOfDay(onDay, ianaTimeZone);

    assertThat(result.getSeconds()).isEqualTo(expectedDurationSeconds);
    assertThat(result.getNano()).isZero();
  }

  public static Stream<Arguments> for_startOfDay() {
    return Stream.of(
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:00+01:00"), "2022-11-22T00:00+01:00"),
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:01+02:00"), "2022-11-22T00:00+02:00"),
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:01:05+04:00"), "2022-11-22T00:00+04:00"),
        Arguments.of(OffsetDateTime.parse("2022-06-22T01:05:10+06:00"), "2022-06-22T00:00+06:00"),
        Arguments.of(OffsetDateTime.parse("2022-06-22T04:10:15+07:00"), "2022-06-22T00:00+07:00"),
        Arguments.of(OffsetDateTime.parse("2022-06-22T06:15:20+09:00"), "2022-06-22T00:00+09:00"),
        Arguments.of(OffsetDateTime.parse("2022-03-27T10:20:25+10:00"), "2022-03-27T00:00+10:00"),
        Arguments.of(OffsetDateTime.parse("2022-03-27T12:25:30+11:00"), "2022-03-27T00:00+11:00"),
        Arguments.of(OffsetDateTime.parse("2022-03-27T15:30:35+11:30"), "2022-03-27T00:00+11:30"),
        Arguments.of(OffsetDateTime.parse("2022-10-30T18:35:40-01:00"), "2022-10-30T00:00-01:00"),
        Arguments.of(OffsetDateTime.parse("2022-10-30T20:40:45-05:00"), "2022-10-30T00:00-05:00"),
        Arguments.of(OffsetDateTime.parse("2022-10-30T22:45:50-08:00"), "2022-10-30T00:00-08:00"),
        Arguments.of(OffsetDateTime.parse("2022-10-30T23:59:59.999999999-11:00"), "2022-10-30T00:00-11:00")
    );
  }

  @ParameterizedTest
  @MethodSource("for_startOfDay")
  void startOfDay_forOffsetDateTime(OffsetDateTime pointInTime, String expectedStartOfDayAsString) {
    var result = DateTimeCalculation.startOfDay(pointInTime);

    assertThat(result).hasToString(expectedStartOfDayAsString);
  }


  public static Stream<Arguments> for_ispEndInDay_differentIspDurations() {
    return Stream.of(
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:00+01:00"), 1, 10, "2022-11-22T00:10+01:00"),
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:01+01:00"), 1, 15, "2022-11-22T00:15+01:00"),
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 1, 20, "2022-11-22T00:20+01:00"),
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:10+02:00"), 1, 30, "2022-06-22T00:30+02:00"),
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:15+02:00"), 1, 60, "2022-06-22T01:00+02:00"),
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 1, 90, "2022-06-22T01:30+02:00")
    );
  }

  @ParameterizedTest
  @MethodSource("for_ispEndInDay_differentIspDurations")
  void ispEndInDay_differentIspDurations(OffsetDateTime onDay, long ofOneBasedIspIndex, long minutesIspDuration, String expectedEndTimeAsString) {
    var ispDuration = Duration.ofMinutes(minutesIspDuration);

    var result = DateTimeCalculation.ispEndInDay(onDay, "Europe/Amsterdam", ofOneBasedIspIndex, ispDuration);

    assertThat(result).hasToString(expectedEndTimeAsString);
  }

  public static Stream<Arguments> for_ispEndInDay() {
    return Stream.of(
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:00+01:00"), 4, 15, "2022-11-22T01:00+01:00"), // 1
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:01+01:00"), 7, 15, "2022-11-22T01:45+01:00"), // 2
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 8, 15, "2022-11-22T02:00+01:00"), // 3
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 9, 15, "2022-11-22T02:15+01:00"), // 4
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 10, 15, "2022-11-22T02:30+01:00"), // 5
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 11, 15, "2022-11-22T02:45+01:00"), // 6
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 12, 15, "2022-11-22T03:00+01:00"), // 7
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 13, 15, "2022-11-22T03:15+01:00"), // 8
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 40, 15, "2022-11-22T10:00+01:00"), // 9
        Arguments.of(OffsetDateTime.parse("2022-11-22T00:00:05+01:00"), 60, 15, "2022-11-22T15:00+01:00"), // 10

        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:10+02:00"), 4, 15, "2022-06-22T01:00+02:00"), // 11
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:15+02:00"), 7, 15, "2022-06-22T01:45+02:00"), // 12
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 8, 15, "2022-06-22T02:00+02:00"), // 13
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 9, 15, "2022-06-22T02:15+02:00"), // 14
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 10, 15, "2022-06-22T02:30+02:00"), // 15
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 11, 15, "2022-06-22T02:45+02:00"), // 16
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 12, 15, "2022-06-22T03:00+02:00"), // 17
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 13, 15, "2022-06-22T03:15+02:00"), // 18
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 40, 15, "2022-06-22T10:00+02:00"), // 19
        Arguments.of(OffsetDateTime.parse("2022-06-22T00:00:20+02:00"), 60, 15, "2022-06-22T15:00+02:00"), // 20

        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:25+01:00"), 4, 15, "2022-03-27T01:00+01:00"), // 21
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:25+01:00"), 7, 15, "2022-03-27T01:45+01:00"), // 22
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:25+01:00"), 8, 15, "2022-03-27T03:00+02:00"), // 23
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:25+01:00"), 9, 15, "2022-03-27T03:15+02:00"), // 24
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:25+01:00"), 10, 15, "2022-03-27T03:30+02:00"), // 25
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:25+01:00"), 11, 15, "2022-03-27T03:45+02:00"), // 26
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:25+01:00"), 12, 15, "2022-03-27T04:00+02:00"), // 27
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:25+01:00"), 13, 15, "2022-03-27T04:15+02:00"), // 28
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:30+01:00"), 40, 15, "2022-03-27T11:00+02:00"), // 29
        Arguments.of(OffsetDateTime.parse("2022-03-27T00:00:35+01:00"), 60, 15, "2022-03-27T16:00+02:00"), // 30

        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:40+02:00"), 4, 15, "2022-10-30T01:00+02:00"), // 31
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:40+02:00"), 7, 15, "2022-10-30T01:45+02:00"), // 32
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:40+02:00"), 8, 15, "2022-10-30T02:00+02:00"), // 33
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:40+02:00"), 9, 15, "2022-10-30T02:15+02:00"), // 34
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:40+02:00"), 10, 15, "2022-10-30T02:30+02:00"), // 35
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:40+02:00"), 11, 15, "2022-10-30T02:45+02:00"), // 36
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:40+02:00"), 12, 15, "2022-10-30T02:00+01:00"), // 37
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:40+02:00"), 13, 15, "2022-10-30T02:15+01:00"), // 38
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:45+02:00"), 40, 15, "2022-10-30T09:00+01:00"), // 39
        Arguments.of(OffsetDateTime.parse("2022-10-30T00:00:50+02:00"), 60, 15, "2022-10-30T14:00+01:00") // 40
    );
  }

  @ParameterizedTest
  @MethodSource("for_ispEndInDay")
  void ispEndInDay(OffsetDateTime onDay, long ofOneBasedIspIndex, long minutesIspDuration, String expectedEndTimeAsString) {
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