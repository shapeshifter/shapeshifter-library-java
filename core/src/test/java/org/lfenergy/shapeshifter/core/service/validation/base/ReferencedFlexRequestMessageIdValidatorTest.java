// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexRequestMessageIdValidatorTest {

  private static final String FLEX_REQUEST_MESSAGE_ID = "FLEX_REQUEST_MESSAGE_ID";
  private static final String CONVERSATION_ID = "CONVERSATION_ID";
  @Mock
  private UftpMessageSupport messageSupport;

  @InjectMocks
  private ReferencedFlexRequestMessageIdValidator testSubject;

  private final UftpParticipant sender = new UftpParticipant("example.com", USEFRoleType.DSO);
  private final FlexOffer flexOffer = new FlexOffer();
  private final FlexRequest flexRequest = new FlexRequest();

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessageResponse.class)).isFalse();
  }

  @Test
  void valid_whenNoReferenceInRequest() {
    given(flexOffer.getFlexRequestMessageID()).willReturn(null);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isTrue();
  }

  @Test
  void valid_whenReferenceInRequestIsKnown() {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);

    flexOffer.setFlexRequestMessageID(FLEX_REQUEST_MESSAGE_ID);
    flexOffer.setConversationID(CONVERSATION_ID);
    given(messageSupport.getPreviousMessage(CONVERSATION_ID, uftpMessage.referenceToPreviousMessage(FLEX_REQUEST_MESSAGE_ID, CONVERSATION_ID,
            FlexRequest.class))).willReturn(Optional.of(flexRequest));

    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @Test
  void invalid_whenReferenceInRequestIsNotKnown() {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);

    flexOffer.setFlexRequestMessageID(FLEX_REQUEST_MESSAGE_ID);
    flexOffer.setConversationID(CONVERSATION_ID);
    given(messageSupport.getPreviousMessage(CONVERSATION_ID, uftpMessage.referenceToPreviousMessage(FLEX_REQUEST_MESSAGE_ID, CONVERSATION_ID,
            FlexRequest.class))).willReturn(Optional.empty());

    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference FlexRequestMessageID");
  }
}