// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOffer;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOfferOption;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOfferOptionIsp;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOfferOptionIsps;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOrder;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOrderIsps;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.messageId;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlexOrderFlexibilityMatchValidatorTest {

  @Mock
  private UftpParticipant sender;

  @Mock
  private UftpMessageSupport messageSupport;
  @InjectMocks
  private FlexOrderFlexibilityMatchValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Ordered flexibility does not match the offered flexibility");
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
  void test_happy_flow_1_order_has_same_flexibility_as_offer() {
    var flexMessageId = messageId();
    var flexOrder = flexOrder(flexMessageId);
    var flexOfferOptionIsps = flexOfferOptionIsps();
    // same as the flex offer....
    flexOfferOptionIsps.add(flexOfferOptionIsp(19, 1, 1500000));
    var flexOffer = flexOffer(flexMessageId,
                              List.of(flexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @Test
  void test_flex_order_has_no_offer_referring_to_existing_offer_then_fail() {
    var messageId = messageId();
    var flexOrder = flexOrder(messageId);
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);
    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(messageId, FlexOffer.class))).willReturn(Optional.empty());
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_one_power_differs_then_fail() {
    var flexMessageId = messageId();
    var flexOrder = flexOrder(flexMessageId);
    var flexOfferOptionIsps = flexOfferOptionIsps();
    // only the start is different....
    flexOfferOptionIsps.add(flexOfferOptionIsp(19, 1, 1000000));
    var flexOffer = flexOffer(flexMessageId,
                              List.of(flexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_no_option_reference_more_than_one_option_then_fail() {
    var flexMessageId = messageId();
    var flexOrder = flexOrder(flexMessageId);
    var flexOfferOptionIsps = flexOfferOptionIsps();
    // only the start is different....
    flexOfferOptionIsps.add(flexOfferOptionIsp(19, 1, 1000000));
    var flexOffer = flexOffer(flexMessageId,
                              List.of(flexOfferOption(flexOfferOptionIsps)));

    flexOrder.getISPS().addAll(flexOrderIsps());
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }
}
