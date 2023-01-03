package org.lfenergy.shapeshifter.api.datetime;

import static java.time.temporal.ChronoField.NANO_OF_DAY;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeCalculation {

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

  public static ZonedDateTime toZonedDateTime(OffsetDateTime pointInTime, String ianaTimeZone) {
    return pointInTime.atZoneSameInstant(toZoneId(ianaTimeZone));
  }

  public static OffsetDateTime startOfDay(OffsetDateTime pointInTime) {
    return pointInTime.with(NANO_OF_DAY, 0);
  }

  public static ZonedDateTime startOfDay(ZonedDateTime pointInTime) {
    return pointInTime.with(NANO_OF_DAY, 0);
  }

  public static Duration lengthOfDay(OffsetDateTime onDay, String ianaTimeZone) {
    OffsetDateTime startOfDay = startOfDay(onDay);
    ZonedDateTime zonedDay = toZonedDateTime(startOfDay, ianaTimeZone);
    return Duration.between(zonedDay, zonedDay.plusDays(1));
  }

  public static OffsetDateTime ispEndInDay(
      OffsetDateTime onDay, String ianaTimeZone, long ofOneBasedIspIndex, Duration ispDuration
  ) {
    var toAdd = ispDuration.multipliedBy(ofOneBasedIspIndex);
    return startOfDay(toZonedDateTime(onDay, ianaTimeZone))
        .plus(toAdd)
        .toOffsetDateTime();
  }
}
