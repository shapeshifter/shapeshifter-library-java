// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOffer;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOfferOption;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOfferOptions;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexRequest;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexRequestIsp;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.messageId;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlexOptionRequestMatchValidatorTest {

  @Mock
  private UftpParticipant sender;

  @Mock
  private UftpMessageSupport messageSupport;
  @InjectMocks
  private FlexOptionRequestMatchValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("None of the ISPs with a 'requested' disposition in the referred FlexRequest, is mentioned in the FlexOffer");
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(FlexOrder.class)).isFalse();
  }

  @Test
  void test_happy_flow_1_request_isps_all_occur_in_flex_offer() {
    var flexMessageId = messageId();
    var flexRequest = flexRequest(flexMessageId, OffsetDateTime.now().plusDays(1));
    var flexOffer = flexOffer(flexMessageId, flexOfferOptions(BigDecimal.valueOf(1.0)));

    flexRequest.getISPS().add(
        flexRequestIsp(AvailableRequestedType.REQUESTED, 12, 1, -5000000, 0)
    );

    flexOffer.getOfferOptions().add(flexOfferOption("REFERENCE_1", BigDecimal.valueOf(1.0), BigDecimal.valueOf(50.0)));
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);
    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexRequest.class))).willReturn(Optional.of(flexRequest));
    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @Test
  void test_happy_flow_2_unsolicited_flex_offer() {
    var flexMessageId = messageId();
    var flexOffer = flexOffer(flexMessageId, flexOfferOptions(BigDecimal.valueOf(1.0)));
    flexOffer.setFlexRequestMessageID(null);
    flexOffer.getOfferOptions().add(flexOfferOption("REFERENCE_1", BigDecimal.valueOf(1.0), BigDecimal.valueOf(50.0)));

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isTrue();

    verifyNoInteractions(messageSupport);
  }

  @Test
  void test_happy_flow_2_flex_request_not_found() {
    var flexMessageId = messageId();
    var flexOffer = flexOffer(flexMessageId, flexOfferOptions(BigDecimal.valueOf(1.0)));

    flexOffer.getOfferOptions().add(flexOfferOption("REFERENCE_1", BigDecimal.valueOf(1.0), BigDecimal.valueOf(50.0)));
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);
    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexRequest.class))).willReturn(Optional.empty());
    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @Test
  void test_offer_isps_have_disposition_available() {
    var flexMessageId = messageId();
    var flexRequest = flexRequest(flexMessageId, OffsetDateTime.now().plusDays(1));
    var flexOffer = flexOffer(flexMessageId, flexOfferOptions(BigDecimal.valueOf(1.0)));

    flexRequest.getISPS().add(
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 12, 1, -5000000, 0)
    );

    flexOffer.getOfferOptions().add(flexOfferOption("REFERENCE_1", BigDecimal.valueOf(1.0), BigDecimal.valueOf(50.0)));
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);
    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexRequest.class))).willReturn(Optional.of(flexRequest));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  @Test
  void test_offer_isps_does_not_match_isp_in_request() {
    var flexMessageId = messageId();
    var flexRequest = flexRequest(flexMessageId, OffsetDateTime.now().plusDays(1));
    var flexOffer = flexOffer(flexMessageId, flexOfferOptions(BigDecimal.valueOf(1.0)));

    flexRequest.getISPS().add(
        flexRequestIsp(AvailableRequestedType.REQUESTED, 23, 2, -5000000, 0)
    );

    flexOffer.getOfferOptions().add(flexOfferOption("REFERENCE_1", BigDecimal.valueOf(1.0), BigDecimal.valueOf(50.0)));
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);
    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexRequest.class))).willReturn(Optional.of(flexRequest));
    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }
}
