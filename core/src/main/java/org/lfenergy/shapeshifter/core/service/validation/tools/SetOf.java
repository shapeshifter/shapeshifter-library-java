// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.tools;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Optional;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SetOf {

  public static <T> Set<T> setOfNullable(T value) {
    return setOfOptional(Optional.ofNullable(value));
  }

  public static <T> Set<T> setOfOptional(Optional<T> optional) {
    return optional.stream().collect(toUnmodifiableSet());
  }
}
