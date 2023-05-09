// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexRequest;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.flexRequestIsp;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IspRequestedDispositionValidatorTest {

  @Mock
  private UftpParticipant sender;

  @InjectMocks
  private IspRequestedDispositionValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Lacking Requested disposition");
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
  void test_happy_flow_one_requested_disposition() {
    var flexRequest = flexRequest();
    flexRequest.getISPS().addAll(List.of(
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 1, 1, -5000000, 0),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 2, 1, 0, 5000000),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 3, 1, -5000000, 5000000),
        flexRequestIsp(AvailableRequestedType.REQUESTED, 4, 1, -1000000, 0)
    ));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexRequest))).isTrue();
  }

  @Test
  void test_fail_no_request_disposition() {
    var flexRequest = flexRequest();
    flexRequest.getISPS().addAll(List.of(
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 1, 1, -5000000, 0),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 2, 1, 0, 5000000),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 3, 1, -5000000, 5000000),
        flexRequestIsp(AvailableRequestedType.AVAILABLE, 4, 1, -5000000, 5000000)
    ));
    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexRequest))).isFalse();
  }
}