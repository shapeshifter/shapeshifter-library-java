// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.conversion;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateAdapter {

  private DateAdapter() {
    // private constructor to hide implicit public one
  }

  private static final DateTimeFormatter XSD_DATE = new DateTimeFormatterBuilder()
      .append(ISO_LOCAL_DATE)
      .optionalStart()
      .appendOffsetId()
      .optionalEnd()
      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
      .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
      .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
      .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
      .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
      .toFormatter();

  public static OffsetDateTime parse(String value) {
    return value != null ? OffsetDateTime.parse(value, XSD_DATE) : null;
  }

  public static String print(OffsetDateTime value) {
    return value != null ? value.format(XSD_DATE) : null;
  }
}
