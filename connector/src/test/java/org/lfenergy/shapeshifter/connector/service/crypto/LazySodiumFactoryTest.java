// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import com.goterl.lazysodium.LazySodiumJava;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LazySodiumFactoryTest {

  @InjectMocks
  private LazySodiumFactory testSubject;

  @Test
  void keyFromBase64String() throws Exception {
    LazySodiumJava lazySodium = new LazySodiumBase64Pool().claim();
    com.goterl.lazysodium.utils.KeyPair keyPair = lazySodium.cryptoSignKeypair();
    String base64Key = Base64.getEncoder().encodeToString(keyPair.getPublicKey().getAsBytes());

    var result = testSubject.keyFromBase64String(base64Key);

    assertThat(result).isEqualTo(keyPair.getPublicKey());
  }
}