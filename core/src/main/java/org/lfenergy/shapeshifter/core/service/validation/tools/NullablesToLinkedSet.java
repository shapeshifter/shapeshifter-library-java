// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.tools;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

// https://www.amitph.com/java-streams-custom-collector/
public class NullablesToLinkedSet<V> implements Collector<V, Set<V>, Set<V>> {

  public static <V> NullablesToLinkedSet<V> toSetIgnoreNulls() {
    return new NullablesToLinkedSet<>();
  }

  @Override
  public Supplier<Set<V>> supplier() {
    return LinkedHashSet::new;
  }

  @Override
  public BiConsumer<Set<V>, V> accumulator() {
    return (set, value) -> {
      var o = Optional.ofNullable(value);
      o.ifPresent(set::add);
    };
  }

  @Override
  public BinaryOperator<Set<V>> combiner() {
    return (set1, set2) -> {
      set1.addAll(set2);
      return set1;
    };
  }

  @Override
  public Function<Set<V>, Set<V>> finisher() {
    return Collections::unmodifiableSet;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Set.of();
  }
}
