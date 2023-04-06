// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexRequest;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexRequestIsp;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IspPowerDiscrepancyValidatorTest {

  @Mock
  private UftpParticipant sender;

  @InjectMocks
  private IspPowerDiscrepancyValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("One or more ISPs with a 'Requested' disposition has no direction");
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexRequest.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(FlexOrder.class)).isFalse();
  }

  @Test
  void test_happy_flow_1_only_available_disposition_isp() {
    var flexRequest = flexRequest();
    flexRequest.getISPS().addAll(List.of(
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 1, 1, -5000000, 0),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 2, 1, 0, 5000000),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 3, 1, -5000000, 5000000)
    ));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexRequest))).isTrue();
  }

  @Test
  void test_happy_flow_2_requested_disposition_either_min_power_or_max_power() {
    var flexRequest = flexRequest();
    flexRequest.getISPS().addAll(List.of(
        flexRequestIsp(AvailableRequestedType.REQUESTED, 1, 1, -5000000, 0),
        flexRequestIsp(AvailableRequestedType.REQUESTED, 2, 1, 0, 5000000),
        flexRequestIsp(AvailableRequestedType.REQUESTED, 3, 1, -5000000, 0)
    ));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexRequest))).isTrue();
  }

  // IDCONS-6422: should be able to send an ISP with only 0
  @Test
  void test_happy_flow_3_requested_disposition_both_0() {
    var flexRequest = flexRequest();
    flexRequest.getISPS().addAll(List.of(
        flexRequestIsp(AvailableRequestedType.REQUESTED, 1, 1, 0, 0),
        flexRequestIsp(AvailableRequestedType.REQUESTED, 2, 1, 0, 0),
        flexRequestIsp(AvailableRequestedType.REQUESTED, 3, 1, 0, 0)
    ));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexRequest))).isTrue();
  }

  @Test
  void test_fail_min_power_and_max_power_both_specified() {
    var flexRequest = flexRequest();
    flexRequest.getISPS().addAll(List.of(
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 1, 1, -5000000, 0),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 2, 1, 0, 5000000),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 3, 1, -5000000, 5000000),
        flexRequestIsp(AvailableRequestedType.REQUESTED, 4, 1, -5000000, 5000000)
    ));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexRequest))).isFalse();
  }

}