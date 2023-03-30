// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.common.collection;

public interface InstancePool<T> {

  public int size();

  public T claim();

  public void release(final T instance);
}
