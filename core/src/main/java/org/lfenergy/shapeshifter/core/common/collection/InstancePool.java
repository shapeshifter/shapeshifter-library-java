// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.collection;

public interface InstancePool<T> {

  int size();

  T claim();

  void release(final T instance);
}
