package org.lfenergy.shapeshifter.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UftpParticipantInformationTest {

  public static final String DOMAIN = "DOMAIN";
  public static final String ENDPOINT = "ENDPOINT";
  public static final String PUBLIC_KEY = "PUBLIC_KEY";

  @Test
  void construction() {
    var testSubject = new UftpParticipantInformation(DOMAIN, PUBLIC_KEY, ENDPOINT);

    assertThat(testSubject.domain()).isEqualTo(DOMAIN);
    assertThat(testSubject.publicKey()).isEqualTo(PUBLIC_KEY);
    assertThat(testSubject.endpoint()).isEqualTo(ENDPOINT);
  }
}