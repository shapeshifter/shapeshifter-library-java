package org.lfenergy.shapeshifter.connector.common.collection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AbstractInstancePoolTest {

  class MyInstancePool extends AbstractInstancePool<String> {

    private int counter = 0;

    @Override
    protected String create() {
      return "Instance" + (++counter);
    }
  }

  @Test
  public void test() {
    final MyInstancePool testSubject = new MyInstancePool();
    assertThat(testSubject.size()).isEqualTo(0);

    // Claim first. It will be created
    final String f1 = testSubject.claim();
    assertThat(testSubject.size()).isEqualTo(0);
    assertThat(f1).isNotNull();
    assertThat(f1).isEqualTo("Instance1");
    // Claim second. It will be created
    final String f2 = testSubject.claim();
    assertThat(testSubject.size()).isEqualTo(0);
    assertThat(f2).isNotNull();
    assertThat(f2).isEqualTo("Instance2");
    // Release second
    testSubject.release(f2);
    assertThat(testSubject.size()).isEqualTo(1);
    // Claim third. It will be the second created
    final String f3 = testSubject.claim();
    assertThat(testSubject.size()).isEqualTo(0);
    assertThat(f3).isSameAs(f2);
    assertThat(f3).isEqualTo("Instance2");
    // Release first
    testSubject.release(f1);
    assertThat(testSubject.size()).isEqualTo(1);
    // Claim fourth. It will be the first created
    final String f4 = testSubject.claim();
    assertThat(testSubject.size()).isEqualTo(0);
    assertThat(f4).isSameAs(f1);
    assertThat(f4).isEqualTo("Instance1");

    testSubject.release(f1);
    assertThat(testSubject.size()).isEqualTo(1);
    testSubject.release(f2);
    assertThat(testSubject.size()).isEqualTo(2);
    testSubject.release(f3);
    assertThat(testSubject.size()).isEqualTo(2);
    testSubject.release(f4);
    assertThat(testSubject.size()).isEqualTo(2);

    // Release is instance compare based. Not equals compare based.
    final String s1 = new String("Instance1");
    testSubject.release(s1);
    assertThat(testSubject.size()).isEqualTo(3);
    final String s2 = new String("Instance1");
    testSubject.release(s2);
    assertThat(testSubject.size()).isEqualTo(4);

    // Release a null pointer. No problem
    testSubject.release(null);
    assertThat(testSubject.size()).isEqualTo(4);
  }
}