// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.ParticipantSupport;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AllowedSenderValidatorTest {

  private static final String SENDER_DOMAIN = "SENDER_DOMAIN";

  @Mock
  private ParticipantSupport participantSupport;

  @InjectMocks
  private AllowedSenderValidator testSubject;

  @Mock
  private UftpParticipant uftpParticipant;
  @Mock
  private PayloadMessageType payloadMessage;

  @Captor
  private ArgumentCaptor<UftpParticipant> sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        participantSupport,
        uftpParticipant,
        payloadMessage
    );
  }

  private void mockSender() {
    given(uftpParticipant.domain()).willReturn(SENDER_DOMAIN);
    given(uftpParticipant.role()).willReturn(USEFRoleType.DSO);
  }

  private void verifySender() {
    verify(participantSupport).isAllowedSender(sender.capture());
    assertThat(sender.getValue().domain()).isEqualTo(SENDER_DOMAIN);
    assertThat(sender.getValue().role()).isEqualTo(USEFRoleType.DSO);
  }

  @Test
  void appliesTo_allTypes() {
    assertThat(testSubject.appliesTo(PayloadMessageType.class)).isTrue();
  }

  @Test
  void valid_true_whenAllowed() {
    mockSender();
    given(participantSupport.isAllowedSender(any(UftpParticipant.class))).willReturn(true);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(uftpParticipant, payloadMessage))).isTrue();

    verifySender();
  }

  @Test
  void valid_false_whenNotAllowed() {
    mockSender();
    given(participantSupport.isAllowedSender(any(UftpParticipant.class))).willReturn(false);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(uftpParticipant, payloadMessage))).isFalse();

    verifySender();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Barred Sender");
  }
}