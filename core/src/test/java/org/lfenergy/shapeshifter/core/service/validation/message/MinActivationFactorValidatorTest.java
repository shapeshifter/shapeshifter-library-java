// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexOffer;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MinActivationFactorValidatorTest {

  @Mock
  private UftpParticipant sender;

  @InjectMocks
  private MinActivationFactorValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Min activation factor must be between 0 and 1 (inclusive)");
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(FlexRequest.class)).isFalse();
  }

  @Test
  void test_happy_flow_one_missing_min_activation_factor() {
    var flexOffer = flexOffer(null);
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isTrue();
  }

  @Test
  void test_happy_flow_two_correct_min_activation_factor() {
    var flexOffer = flexOffer(BigDecimal.valueOf(0.5));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isTrue();
  }

  @Test
  void test_happy_flow_three_correct_min_activation_factor() {
    var flexOffer = flexOffer(BigDecimal.valueOf(1.0));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isTrue();
  }

  @Test
  void test_min_activation_cannot_be_0() {
    var flexOffer = flexOffer(BigDecimal.valueOf(0));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isFalse();
  }

  @Test
  void test_min_activation_cannot_be_smaller_than_0() {
    var flexOffer = flexOffer(BigDecimal.valueOf(-1.0));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isFalse();
  }

  @Test
  void test_min_activation_cannot_be_greater_than_1() {
    var flexOffer = flexOffer(BigDecimal.valueOf(1.1));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isFalse();
  }
}