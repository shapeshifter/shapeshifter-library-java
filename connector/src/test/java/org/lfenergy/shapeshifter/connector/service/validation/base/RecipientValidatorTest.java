// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.ParticipantSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecipientValidatorTest {

  private static final String RECIPIENT_DOMAIN = "RECIPIENT_DOMAIN";

  @Mock
  private ParticipantSupport participantSupport;

  @InjectMocks
  private RecipientValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private PayloadMessageType payloadMessage;

  private static final UftpParticipant RECIPIENT_AGR = new UftpParticipant(RECIPIENT_DOMAIN, USEFRoleType.AGR);
  private static final UftpParticipant RECIPIENT_DSO = new UftpParticipant(RECIPIENT_DOMAIN, USEFRoleType.DSO);

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        participantSupport,
        sender,
        payloadMessage
    );
  }

  @Test
  void appliesTo_allTypes() {
    assertThat(testSubject.appliesTo(PayloadMessageType.class)).isTrue();
  }

  @Test
  void valid_when_handled_agr() {
    given(payloadMessage.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);
    given(sender.role()).willReturn(USEFRoleType.DSO);
    given(participantSupport.isHandledRecipient(RECIPIENT_AGR)).willReturn(true);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
    verify(participantSupport).isHandledRecipient(RECIPIENT_AGR);
  }

  @Test
  void valid_when_handled_dso() {
    given(payloadMessage.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);
    given(sender.role()).willReturn(USEFRoleType.AGR);
    given(participantSupport.isHandledRecipient(RECIPIENT_DSO)).willReturn(true);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
    verify(participantSupport).isHandledRecipient(RECIPIENT_DSO);
  }

  @Test
  void valid_false_when_not_handled_agr() {
    given(payloadMessage.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);
    given(sender.role()).willReturn(USEFRoleType.DSO);
    given(participantSupport.isHandledRecipient(RECIPIENT_AGR)).willReturn(false);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
    verify(participantSupport).isHandledRecipient(RECIPIENT_AGR);
  }

  @Test
  void valid_false_when_not_handled_dso() {
    given(payloadMessage.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);
    given(sender.role()).willReturn(USEFRoleType.AGR);
    given(participantSupport.isHandledRecipient(RECIPIENT_DSO)).willReturn(false);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
    verify(participantSupport).isHandledRecipient(RECIPIENT_DSO);
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown Recipient");
  }
}