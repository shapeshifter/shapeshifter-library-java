// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.utils.Base64MessageEncoder;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class LazySodiumBase64PoolTest {

  @Test
  void correctTypeIsCreated() throws Exception {
    final LazySodiumBase64Pool testSubject = new LazySodiumBase64Pool();
    final LazySodiumJava v1 = testSubject.claim();

    assertThat(v1.getSodium()).isInstanceOf(SodiumJava.class);
    Field messageEncoder = v1.getClass().getSuperclass().getDeclaredField("messageEncoder");
    messageEncoder.setAccessible(true);
    assertThat(messageEncoder.get(v1)).isInstanceOf(Base64MessageEncoder.class);
  }

  @Test
  void claimedTwice_resultIsDifferentInstances() throws Exception {
    final LazySodiumBase64Pool testSubject = new LazySodiumBase64Pool();
    final LazySodiumJava v1 = testSubject.claim();
    final LazySodiumJava v2 = testSubject.claim();

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v2).isNotSameAs(v1);
  }

  @Test
  void claimReleaseClaim_resultBothAreSameInstance() {
    final LazySodiumBase64Pool testSubject = new LazySodiumBase64Pool();
    final LazySodiumJava v1 = testSubject.claim();
    testSubject.release(v1);
    final LazySodiumJava v2 = testSubject.claim();

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v2).isSameAs(v1);
  }
}