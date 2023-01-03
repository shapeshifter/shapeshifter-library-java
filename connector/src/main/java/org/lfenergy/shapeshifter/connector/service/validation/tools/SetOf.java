package org.lfenergy.shapeshifter.connector.service.validation.tools;

import static java.util.stream.Collectors.toUnmodifiableSet;

import java.util.Optional;
import java.util.Set;

public class SetOf {

  public static <T> Set<T> setOfNullable(T value) {
    return setOfOptional(Optional.ofNullable(value));
  }

  public static <T> Set<T> setOfOptional(Optional<T> optional) {
    return optional.stream().collect(toUnmodifiableSet());
  }
}
