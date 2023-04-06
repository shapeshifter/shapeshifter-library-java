// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.datetime;

import static java.time.temporal.ChronoField.NANO_OF_DAY;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeCalculation {

  private DateTimeCalculation() {
    // private constructor to hide implicit public one
  }

  public static Duration toJavaTime(javax.xml.datatype.Duration duration) {
    if (!canBeConvertedWithoutOffset(duration)) {
      throw new IllegalArgumentException("Duration cannot be converted without offset because it contains months and/or years.");
    }
    return Duration.ofDays(duration.getDays())
                   .plusHours(duration.getHours())
                   .plusMinutes(duration.getMinutes())
                   .plusSeconds(duration.getSeconds());
  }

  public static boolean canBeConvertedWithoutOffset(javax.xml.datatype.Duration duration) {
    return duration.getYears() == 0 && duration.getMonths() == 0;
  }

  public static ZoneId toZoneId(String ianaTimeZone) {
    return ZoneId.of(ianaTimeZone);
  }

  static ZonedDateTime toZonedDateTime(LocalDate pointInTime, String ianaTimeZone) {
    return pointInTime.atStartOfDay(toZoneId(ianaTimeZone));
  }

  public static ZonedDateTime startOfDay(ZonedDateTime pointInTime) {
    return pointInTime.with(NANO_OF_DAY, 0);
  }

  public static Duration lengthOfDay(LocalDate onDay, String ianaTimeZone) {
    ZonedDateTime zonedDay = toZonedDateTime(onDay, ianaTimeZone);
    return Duration.between(zonedDay, zonedDay.plusDays(1));
  }

  public static OffsetDateTime ispEndInDay(
      LocalDate onDay, String ianaTimeZone, long ofOneBasedIspIndex, Duration ispDuration
  ) {
    var toAdd = ispDuration.multipliedBy(ofOneBasedIspIndex);
    return startOfDay(toZonedDateTime(onDay, ianaTimeZone))
        .plus(toAdd)
        .toOffsetDateTime();
  }
}
