// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.DEFAULT_MIN_ACTIVATION_FACTOR;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.DEFAULT_OPTION_REFERENCE;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.DEFAULT_PRICE;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.conversationId;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOffer;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOfferOption;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOfferOptionIsp;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOfferOptionIsps;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOrder;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOrderIsps;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.messageId;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlexOrderIspMatchValidatorTest {

  @Mock
  private UftpParticipant sender;

  @Mock
  private UftpMessageSupport messageSupport;
  @InjectMocks
  private FlexOrderIspMatchValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("ISPs from the FlexOrder do not match the ISPs given in the FlexOffer");
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
  void test_happy_flow_1_order_isps_all_occur_in_flex_offer() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId,conversationId);
    var flexOfferOptionIsps = flexOfferOptionIsps();
    // same as the flex offer....
    flexOfferOptionIsps.add(flexOfferOptionIsp(19, 1, 1500000));
    var flexOffer = flexOffer(flexMessageId,
                              conversationId,
                              List.of(getFlexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.findReferenceMessageInConversation(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @Test
  void test_flex_order_has_no_offer_referring_to_existing_offer_then_fail() {
    var messageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(messageId, conversationId);
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);
    given(messageSupport.getPreviousMessage(uftpMessage.findReferenceMessageInConversation(messageId, conversationId, FlexOffer.class))).willReturn(Optional.empty());
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_one_start_isp_differs_then_fail() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId, conversationId);
    var flexOfferOptionIsps = flexOfferOptionIsps();
    // only the start is different....
    flexOfferOptionIsps.add(flexOfferOptionIsp(20, 1, 1500000));
    var flexOffer = flexOffer(flexMessageId,
                              conversationId,
                              List.of(getFlexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.findReferenceMessageInConversation(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_one_duration_isp_differs_then_fail() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId, conversationId);

    var flexOfferOptionIsps = flexOfferOptionIsps();
    // only the duration is different....
    flexOfferOptionIsps.add(flexOfferOptionIsp(19, 2, 1500000));
    var flexOffer = flexOffer(flexMessageId,
                              conversationId,
                              List.of(getFlexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.findReferenceMessageInConversation(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }


  @Test
  void test_one_offer_option_isp_missing_then_fail() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId, conversationId);

    var flexOfferOptionIsps = flexOfferOptionIsps();

    var flexOffer = flexOffer(flexMessageId,
                              conversationId,
                              List.of(getFlexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.findReferenceMessageInConversation(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_one_offer_option_isp_too_much_then_fail() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId, conversationId);

    var flexOfferOptionIsps = flexOfferOptionIsps();
    flexOfferOptionIsps.add(flexOfferOptionIsp(19, 1, 1500000));
    // add an extra offer option ISP, not present in the order...
    flexOfferOptionIsps.add(flexOfferOptionIsp(20, 1, 1500000));

    var flexOffer = flexOffer(flexMessageId,
                              conversationId,
                              List.of(getFlexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.findReferenceMessageInConversation(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_no_option_reference_multiple_offer_options_then_fail() {
    var flexMessageId = messageId();
    var conversationId = conversationId();
    var flexOrder = flexOrder(flexMessageId, conversationId, DEFAULT_PRICE, null);

    var flexOfferOptionIsps = flexOfferOptionIsps();
    flexOfferOptionIsps.add(flexOfferOptionIsp(19, 1, 1500000));
    // add an extra offer option ISP, not present in the order...
    flexOfferOptionIsps.add(flexOfferOptionIsp(20, 1, 1500000));

    var flexOffer = flexOffer(flexMessageId,
                              conversationId,
                              List.of(getFlexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.findReferenceMessageInConversation(flexMessageId, conversationId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  private FlexOfferOptionType getFlexOfferOption(List<FlexOfferOptionISPType> flexOfferOptionIsps) {
    return flexOfferOption(DEFAULT_OPTION_REFERENCE, DEFAULT_MIN_ACTIVATION_FACTOR, DEFAULT_PRICE, flexOfferOptionIsps);
  }
}