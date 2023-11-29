// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.conversationId;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOffer;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOfferOption;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOfferOptionIsps;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOrder;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.messageId;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.DEFAULT_OPTION_REFERENCE;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlexOrderPriceMatchValidatorTest {

  @Mock
  private UftpParticipant sender;

  @Mock
  private UftpMessageSupport messageSupport;
  @InjectMocks
  private FlexOrderPriceMatchValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Price in the order does not match the price given in the offer");
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(FlexRequest.class)).isFalse();
  }

  @Test
  void test_happy_flow_1_order_has_same_price_as_offer() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId, conversationId);
    var flexOffer = flexOffer(flexMessageId,
                              conversationId,
                              List.of(TestDataHelper.flexOfferOption(flexOfferOptionIsps())));
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @Test
  void test_flex_order_has_different_price_than_options_in_offer() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId, conversationId);
    var flexOffer = flexOffer(flexMessageId,
                              conversationId,
                              List.of(flexOfferOption(BigDecimal.valueOf(50.),
                                                      flexOfferOptionIsps())));
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_flex_order_has_no_offer_referring_to_existing_offer_then_fail() {
    var messageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(messageId, conversationId);
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);
    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(messageId, conversationId, FlexOffer.class))).willReturn(Optional.empty());
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_flex_order_price_should_allow_1_decimal_match() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId, conversationId, BigDecimal.valueOf(50.0), DEFAULT_OPTION_REFERENCE);
    var flexOffer = flexOffer(flexMessageId, conversationId,
            List.of(flexOfferOption(new BigDecimal("50.00"),
                    flexOfferOptionIsps())));

    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.findReferencedMessage( uftpMessage.referenceToPreviousMessage(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }
}
