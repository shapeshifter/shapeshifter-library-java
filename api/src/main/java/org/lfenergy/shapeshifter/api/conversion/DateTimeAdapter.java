package org.lfenergy.shapeshifter.api.conversion;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateTimeAdapter {

  private static final DateTimeFormatter XSD_DATE_TIME = new DateTimeFormatterBuilder()
      .append(ISO_LOCAL_DATE_TIME)
      .optionalStart()
      .appendOffsetId()
      .optionalEnd()
      .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
      .toFormatter();

  public static OffsetDateTime parse(String value) {
    return value != null ? OffsetDateTime.parse(value, XSD_DATE_TIME) : null;
  }

  public static String print(OffsetDateTime value) {
    return value != null ? value.format(XSD_DATE_TIME) : null;
  }
}
