// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementStatusType;
import org.lfenergy.shapeshifter.api.FlexSettlementResponse;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexSettlementResponseOrderReferenceValidatorTest {

  private static final String ORDER_REFERENCE1 = "ORDER_REFERENCE1";
  private static final String ORDER_REFERENCE2 = "ORDER_REFERENCE2";
  private static final String RECIPIENT_DOMAIN = "RECIPIENT_DOMAIN";

  @Mock
  private UftpMessageSupport messageSupport;

  @InjectMocks
  private ReferencedFlexSettlementResponseOrderReferenceValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private FlexSettlementResponse settlementResponse;
  @Mock
  private FlexOrderSettlementStatusType status1, status2;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        messageSupport,
        sender,
        settlementResponse,
        status1, status2
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexSettlementResponse.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessageResponse.class)).isFalse();
  }

  @Test
  void valid_whenNoReferencesInResponse() {
    given(settlementResponse.getFlexOrderSettlementStatuses()).willReturn(List.of());

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, settlementResponse))).isTrue();
  }

  @Test
  void valid_whenAllReferencesInListAreKnow() {
    given(settlementResponse.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);
    given(settlementResponse.getFlexOrderSettlementStatuses()).willReturn(List.of(
        status1, status2
    ));
    given(status1.getOrderReference()).willReturn(ORDER_REFERENCE1);
    given(status2.getOrderReference()).willReturn(ORDER_REFERENCE2);
    given(messageSupport.isValidOrderReference(ORDER_REFERENCE1, RECIPIENT_DOMAIN)).willReturn(true);
    given(messageSupport.isValidOrderReference(ORDER_REFERENCE2, RECIPIENT_DOMAIN)).willReturn(true);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, settlementResponse))).isTrue();
  }

  @Test
  void invalid_whenSingleReferenceInListIsUnknown() {
    given(settlementResponse.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);
    given(settlementResponse.getFlexOrderSettlementStatuses()).willReturn(List.of(
        status1, status2
    ));
    given(status1.getOrderReference()).willReturn(ORDER_REFERENCE1);
    given(status2.getOrderReference()).willReturn(ORDER_REFERENCE2);
    given(messageSupport.isValidOrderReference(ORDER_REFERENCE1, RECIPIENT_DOMAIN)).willReturn(true);
    given(messageSupport.isValidOrderReference(ORDER_REFERENCE2, RECIPIENT_DOMAIN)).willReturn(false);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, settlementResponse))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference OrderReference in FlexSettlementResponse");
  }
}