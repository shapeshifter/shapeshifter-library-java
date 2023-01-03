package org.lfenergy.shapeshifter.connector.common.collection;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractInstancePool<T> implements InstancePool<T> {

  private final ConcurrentLinkedQueue<T> instancePool = new ConcurrentLinkedQueue<>();

  protected abstract T create();

  @Override
  @SuppressWarnings("squid:S2250") // Ignore sonar: size() is expensive for large ConcurrentLinkedQueue
  public int size() {
    return instancePool.size();
  }

  @Override
  public T claim() {
    T instance = instancePool.poll();
    if (null == instance) {
      instance = create();
    }
    return instance;
  }

  @Override
  public void release(final T instance) {
    if (null == instance) {
      return;
    }
    // Prevent instances being released twice
    if (!alreadyContains(instance)) {
      instancePool.offer(instance);
    }
  }

  private boolean alreadyContains(final T instance) {
    return instancePool.stream().anyMatch(x -> x == instance);
  }
}
