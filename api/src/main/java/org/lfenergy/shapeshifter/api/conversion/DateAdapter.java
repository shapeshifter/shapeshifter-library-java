// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.conversion;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateAdapter {

  private static final DateTimeFormatter XSD_DATE = new DateTimeFormatterBuilder()
      .append(ISO_LOCAL_DATE)
      .optionalStart()
      .appendOffsetId()
      .toFormatter();

  public static LocalDate parse(String value) {
    return value != null ? LocalDate.parse(value, XSD_DATE) : null;
  }

  public static String print(LocalDate value) {
    return value != null ? value.format(XSD_DATE) : null;
  }
}
