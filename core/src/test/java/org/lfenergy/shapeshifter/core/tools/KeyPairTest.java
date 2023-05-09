// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class KeyPairTest {

  public static final String PUBLIC_KEY = "PUBLIC_KEY";
  public static final String PRIVATE_KEY = "PRIVATE_KEY";

  @Test
  void construction() {
    var testSubject = new KeyPair(PUBLIC_KEY, PRIVATE_KEY);

    assertThat(testSubject.publicKey()).isEqualTo(PUBLIC_KEY);
    assertThat(testSubject.privateKey()).isEqualTo(PRIVATE_KEY);
  }
}