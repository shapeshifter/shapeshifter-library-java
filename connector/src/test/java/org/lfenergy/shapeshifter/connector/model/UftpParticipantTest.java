// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpParticipantTest {

  public static final String DOMAIN = "DOMAIN";
  public static final USEFRoleType ROLE = USEFRoleType.DSO;

  @Mock
  private SignedMessage signedMessage;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(signedMessage);
  }

  @Test
  void construction() {
    var testSubject = new UftpParticipant(DOMAIN, ROLE);

    assertThat(testSubject.domain()).isEqualTo(DOMAIN);
    assertThat(testSubject.role()).isEqualTo(ROLE);
  }

  @Test
  void constructionFromSignedMessage() {
    given(signedMessage.getSenderDomain()).willReturn(DOMAIN);
    given(signedMessage.getSenderRole()).willReturn(ROLE);

    var testSubject = new UftpParticipant(signedMessage);

    assertThat(testSubject.domain()).isEqualTo(DOMAIN);
    assertThat(testSubject.role()).isEqualTo(ROLE);
  }
}