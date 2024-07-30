// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.tools;

import java.time.OffsetDateTime;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeCompareAllowingInfinite {

  public static boolean equalOrAfter(OffsetDateTime equalToOrAfter, OffsetDateTime comparedTo) {
    return isEqual(equalToOrAfter, comparedTo) || isAfter(equalToOrAfter, comparedTo);
  }

  public static boolean isEqual(OffsetDateTime equal, OffsetDateTime comparedTo) {
    if (isInfinite(equal)) {
      return isInfinite(comparedTo);
    }
    // comparedTo may not be infinite because equalToOrAfter is not
    return !isInfinite(comparedTo) && equal.isEqual(comparedTo);
  }

  public static boolean isAfter(OffsetDateTime after, OffsetDateTime comparedTo) {
    if (isInfinite(comparedTo)) {
      return false; // equalToOrAfter can never be after infinite, even if it is infinite itself
    }
    return isInfinite(after) || after.isAfter(comparedTo);
  }

  public static boolean isInfinite(OffsetDateTime value) {
    return value == null;
  }
}
