// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.Random;

class UftpParticipantInformationTest {


  @Test
  void construction() {
    Random random = new Random();
    String domain = "DOMAIN" + random.nextInt();
    String endpoint = "ENDPOINT"  + random.nextInt();
    String publicKey = "PUBLIC_KEY" + random.nextInt();
    boolean requiresAuth = random.nextBoolean();

    var testSubject = new UftpParticipantInformation(domain, publicKey, endpoint, requiresAuth);

    assertThat(testSubject.domain()).isEqualTo(domain);
    assertThat(testSubject.publicKey()).isEqualTo(publicKey);
    assertThat(testSubject.endpoint()).isEqualTo(endpoint);
    assertThat(testSubject.requiresAuthorization()).isEqualTo(requiresAuth);
  }
}