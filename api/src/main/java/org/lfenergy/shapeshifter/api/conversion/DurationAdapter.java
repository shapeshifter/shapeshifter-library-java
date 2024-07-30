// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.conversion;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DurationAdapter {

  public static Duration parse(String str) {
    return str != null ? Duration.parse(str) : null;
  }

  public static String print(Duration duration) {
    return duration != null ? duration.toString() : null;
  }

}
