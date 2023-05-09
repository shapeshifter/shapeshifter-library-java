// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedBaselineReferenceValidatorTest {

  private static final String BASELINE_REFERENCE1 = "BASELINE_REFERENCE1";
  private static final String BASELINE_REFERENCE2 = "BASELINE_REFERENCE2";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private ReferencedBaselineReferenceValidator testSubject;

  @Mock
  private UftpParticipant sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        sender
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexSettlement.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withoutParameter() {
    return Stream.of(
        Arguments.of(new FlexOffer()),
        Arguments.of(new FlexOrder()),
        Arguments.of(new FlexSettlement())
    );
  }

  public static Stream<Arguments> withParameter() {

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setBaselineReference(BASELINE_REFERENCE1);

    FlexOrder flexOrder = new FlexOrder();
    flexOrder.setBaselineReference(BASELINE_REFERENCE1);

    FlexOrderSettlementType t1 = new FlexOrderSettlementType();
    t1.setBaselineReference(BASELINE_REFERENCE1);
    FlexOrderSettlementType t2 = new FlexOrderSettlementType();
    t2.setBaselineReference(BASELINE_REFERENCE2);

    FlexSettlement flexSettlement = new FlexSettlement();
    flexSettlement.getFlexOrderSettlements().addAll(List.of(t1, t2));

    return Stream.of(
        Arguments.of(flexOffer, List.of(BASELINE_REFERENCE1)),
        Arguments.of(flexOrder, List.of(BASELINE_REFERENCE1)),
        Arguments.of(flexSettlement, List.of(BASELINE_REFERENCE1, BASELINE_REFERENCE2))
    );
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenNoValueIsPresent(PayloadMessageType payloadMessage) {
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_true_whenFoundValueIsSupported(PayloadMessageType payloadMessage, List<String> baselineRefs) {
    if (baselineRefs.size() >= 1) {
      given(support.isValidBaselineReference(BASELINE_REFERENCE1)).willReturn(true);
    }
    if (baselineRefs.size() == 2) {
      given(support.isValidBaselineReference(BASELINE_REFERENCE2)).willReturn(true);
    }

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_false_whenFoundValueIsFirstNotSupported(PayloadMessageType payloadMessage, List<String> baselineRefs) {
    given(support.isValidBaselineReference(BASELINE_REFERENCE1)).willReturn(false);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference BaselineReference");
  }
}