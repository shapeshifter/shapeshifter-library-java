// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlexOrderOfferIsNotRevokedValidatorTest {

  private static final String MATCHING_NESSAGE_ID = "MATCHING_MESSAGE_ID";
  private static final String RECIPIENT_DOMAIN = "RECIPIENT_DOMAIN";

  @Mock
  private UftpMessageSupport messageSupport;

  @InjectMocks
  private FlexOrderOfferIsNotRevokedValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private FlexOrder flexOrder;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        messageSupport,
        sender,
        flexOrder
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  @Test
  void valid_whenNotRevoked() {
    given(flexOrder.getFlexOfferMessageID()).willReturn(MATCHING_NESSAGE_ID);
    given(flexOrder.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);

    given(messageSupport.existsFlexRevocation(MATCHING_NESSAGE_ID, RECIPIENT_DOMAIN)).willReturn(false);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOrder))).isTrue();
  }

  @Test
  void invalid_whenRevoked() {
    given(flexOrder.getFlexOfferMessageID()).willReturn(MATCHING_NESSAGE_ID);
    given(flexOrder.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);

    given(messageSupport.existsFlexRevocation(MATCHING_NESSAGE_ID, RECIPIENT_DOMAIN)).willReturn(true);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOrder))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Reference message revoked");
  }
}