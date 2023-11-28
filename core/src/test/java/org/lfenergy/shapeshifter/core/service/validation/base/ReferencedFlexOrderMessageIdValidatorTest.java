// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.conversationId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.DPrognosisResponse;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderStatusType;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexOrderMessageIdValidatorTest {

  private static final String FLEX_ORDER_MESSAGE_ID1 = "FLEX_ORDER_MESSAGE_ID1";
  private static final String FLEX_ORDER_MESSAGE_ID2 = "FLEX_ORDER_MESSAGE_ID2";
  private static final String CONVERSATION_ID = conversationId();

  @Mock
  private UftpMessageSupport messageSupport;

  @InjectMocks
  private ReferencedFlexOrderMessageIdValidator testSubject;

  private final UftpParticipant sender = new UftpParticipant("example.com", USEFRoleType.DSO);
  private final DPrognosisResponse prognosisResponse = new DPrognosisResponse();
  private final FlexOrderStatusType status1 = new FlexOrderStatusType();
  private final FlexOrderStatusType status2 = new FlexOrderStatusType();
  private final FlexOrder flexOrder1 = new FlexOrder();
  private final FlexOrder flexOrder2 = new FlexOrder();

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(messageSupport);
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(DPrognosisResponse.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessageResponse.class)).isFalse();
  }

  @Test
  void valid_whenNoReferencesInResponse() {
    prognosisResponse.getFlexOrderStatuses().clear();

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, prognosisResponse))).isTrue();
  }

  @Test
  void valid_whenAllReferencesInListAreKnow() {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, prognosisResponse);

    prognosisResponse.getFlexOrderStatuses().addAll(List.of(status1, status2));
    prognosisResponse.setConversationID(CONVERSATION_ID);
    status1.setFlexOrderMessageID(FLEX_ORDER_MESSAGE_ID1);
    status2.setFlexOrderMessageID(FLEX_ORDER_MESSAGE_ID2);
    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(FLEX_ORDER_MESSAGE_ID1, CONVERSATION_ID,
            FlexOrder.class))).willReturn(Optional.of(flexOrder1));
    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(FLEX_ORDER_MESSAGE_ID2, CONVERSATION_ID,
            FlexOrder.class))).willReturn(Optional.of(flexOrder2));

    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @Test
  void invalid_whenSingleReferenceInListIsUnknown() {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, prognosisResponse);

    prognosisResponse.getFlexOrderStatuses().addAll(List.of(status1, status2));
    prognosisResponse.setConversationID(CONVERSATION_ID);
    status1.setFlexOrderMessageID(FLEX_ORDER_MESSAGE_ID1);
    status2.setFlexOrderMessageID(FLEX_ORDER_MESSAGE_ID2);
    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(FLEX_ORDER_MESSAGE_ID1, CONVERSATION_ID,
            FlexOrder.class))).willReturn(Optional.of(flexOrder1));
    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(FLEX_ORDER_MESSAGE_ID2, CONVERSATION_ID,
            FlexOrder.class))).willReturn(Optional.empty());

    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference FlexOrderMessageID");
  }
}