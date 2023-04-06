package org.lfenergy.shapeshifter.connector.service.validation.tools;

import java.time.LocalDate;

public class DateCompareAllowingInfinite {

  private DateCompareAllowingInfinite() {
    // private constructor to hide implicit public one
  }

  public static boolean equalOrAfter(LocalDate equalToOrAfter, LocalDate comparedTo) {
    return isEqual(equalToOrAfter, comparedTo) || isAfter(equalToOrAfter, comparedTo);
  }

  public static boolean isEqual(LocalDate equal, LocalDate comparedTo) {
    if (isInfinite(equal)) {
      return isInfinite(comparedTo);
    }
    // comparedTo may not be infinite because equalToOrAfter is not
    return !isInfinite(comparedTo) && equal.isEqual(comparedTo);
  }

  public static boolean isAfter(LocalDate after, LocalDate comparedTo) {
    if (isInfinite(comparedTo)) {
      return false; // equalToOrAfter can never be after infinite, even if it is infinite itself
    }
    return isInfinite(after) || after.isAfter(comparedTo);
  }

  public static boolean isInfinite(LocalDate value) {
    return value == null;
  }

}
