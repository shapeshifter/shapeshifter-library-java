// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessageDirection;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpMessageReference;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexOrderOptionReferenceValidatorTest {

  private static final String FLEX_OFFER_ID = "FLEX_OFFER_ID";
  private static final String OPTION_REFERENCE = "OPTION_REFERENCE";
  private static final String AGR_DOMAIN = "agr.com";
  private static final String DSO_DOMAIN = "dso.com";

  @Mock
  private UftpMessageSupport messageSupport;

  @InjectMocks
  private ReferencedFlexOrderOptionReferenceValidator testSubject;

  private final UftpParticipant sender = new UftpParticipant(AGR_DOMAIN, USEFRoleType.AGR);
  private final FlexOrder flexOrder = new FlexOrder();
  private final FlexOffer flexOffer = new FlexOffer();

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(PayloadMessageType.class)).isFalse();
    assertThat(testSubject.appliesTo(FlexMessageType.class)).isFalse();
    assertThat(testSubject.appliesTo(FlexOffer.class)).isFalse();
  }

  @Test
  void valid_true_whenNoValueIsPresent() {
    flexOrder.setOrderReference(null);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOrder))).isTrue();
  }

  @Test
  void valid_true_whenFlexOfferNotFound() {
    flexOrder.setSenderDomain(DSO_DOMAIN);
    flexOrder.setRecipientDomain(AGR_DOMAIN);
    flexOrder.setOptionReference(OPTION_REFERENCE);
    flexOrder.setFlexOfferMessageID(FLEX_OFFER_ID);

    given(messageSupport.getPreviousMessage(any(UftpMessageReference.class))).willReturn(Optional.empty());

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOrder))).isTrue();
  }

  @Test
  void valid_true_whenFoundValueIsSupported() {
    flexOrder.setSenderDomain(DSO_DOMAIN);
    flexOrder.setRecipientDomain(AGR_DOMAIN);
    flexOrder.setOptionReference(OPTION_REFERENCE);
    flexOrder.setFlexOfferMessageID(FLEX_OFFER_ID);

    var offerOption = new FlexOfferOptionType();
    offerOption.setOptionReference(OPTION_REFERENCE);
    flexOffer.getOfferOptions().add(offerOption);

    given(messageSupport.getPreviousMessage(new UftpMessageReference<>(FLEX_OFFER_ID, UftpMessageDirection.OUTGOING, AGR_DOMAIN, DSO_DOMAIN, FlexOffer.class))).willReturn(
        Optional.of(flexOffer));

    assertThat(testSubject.isValid(UftpMessageFixture.createIncoming(sender, flexOrder))).isTrue();
  }

  @Test
  void valid_false_whenFoundValueIsNotSupported() {
    flexOrder.setSenderDomain(DSO_DOMAIN);
    flexOrder.setRecipientDomain(AGR_DOMAIN);
    flexOrder.setOptionReference(OPTION_REFERENCE);
    flexOrder.setFlexOfferMessageID(FLEX_OFFER_ID);

    var offerOption = new FlexOfferOptionType();
    offerOption.setOptionReference("AnotherOptionReference");
    flexOffer.getOfferOptions().add(offerOption);

    given(messageSupport.getPreviousMessage(new UftpMessageReference<>(FLEX_OFFER_ID, UftpMessageDirection.OUTGOING, AGR_DOMAIN, DSO_DOMAIN, FlexOffer.class))).willReturn(
        Optional.of(flexOffer));

    assertThat(testSubject.isValid(UftpMessageFixture.createIncoming(sender, flexOrder))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference OptionReference in FlexOrder");
  }
}