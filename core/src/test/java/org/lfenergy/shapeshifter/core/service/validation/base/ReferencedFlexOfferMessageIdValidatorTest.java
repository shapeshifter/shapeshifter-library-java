// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.conversationId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferRevocation;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexOfferMessageIdValidatorTest {

  private static final String FLEX_OFFER_MESSAGE_ID = "FLEX_OFFER_MESSAGE_ID";
  private static final String CONVERSATION_ID = conversationId();

  @Mock
  private UftpMessageSupport messageSupport;

  @InjectMocks
  private ReferencedFlexOfferMessageIdValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private FlexOffer flexOffer;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        messageSupport,
        sender,
        flexOffer
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOfferRevocation.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withoutParameter() {
    return Stream.of(
        Arguments.of(new FlexOfferRevocation()),
        Arguments.of(new FlexOrder())
    );
  }

  public static Stream<Arguments> withParameter() {

    FlexOfferRevocation revocation = new FlexOfferRevocation();
    revocation.setFlexOfferMessageID(FLEX_OFFER_MESSAGE_ID);
    revocation.setConversationID(CONVERSATION_ID);

    FlexOrder flexOrder = new FlexOrder();
    flexOrder.setFlexOfferMessageID(FLEX_OFFER_MESSAGE_ID);
    flexOrder.setConversationID(CONVERSATION_ID);

    return Stream.of(
        Arguments.of(revocation),
        Arguments.of(flexOrder)
    );
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenNoValueIsPresent(PayloadMessageType payloadMessage) {
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_true_whenFoundMessageIdIsOfKnownMessage(PayloadMessageType payloadMessage) {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);
    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(FLEX_OFFER_MESSAGE_ID, CONVERSATION_ID,
            FlexOffer.class))).willReturn(Optional.of(flexOffer));

    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_false_whenFoundMessageIdIsOfUnknownMessage(PayloadMessageType payloadMessage) {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);
    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(FLEX_OFFER_MESSAGE_ID, CONVERSATION_ID,
            FlexOffer.class))).willReturn(Optional.empty());

    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference FlexOfferMessageID");
  }
}