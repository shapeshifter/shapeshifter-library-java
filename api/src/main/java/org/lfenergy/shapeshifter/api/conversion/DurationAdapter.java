package org.lfenergy.shapeshifter.api.conversion;

import java.time.Duration;

public class DurationAdapter {

  public static Duration parse(String str) {
    return str != null ? Duration.parse(str) : null;
  }

  public static String print(Duration duration) {
    return duration != null ? duration.toString() : null;
  }

}
