// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IspPeriodBoundaryValidator extends IspListValidatorBase {

  @Override
  public String getReason() {
    return "ISPs out of bounds";
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  protected boolean validateIsps(long maxNumberIsps, List<IspInfo> isps) {
    return isInBounds(maxNumberIsps, isps);
  }

  private boolean isInBounds(long maxNumberIsps, List<IspInfo> isps) {
    return isps.stream().allMatch(isp -> isInBounds(maxNumberIsps, isp));
  }

  private boolean isInBounds(long maxNumberIsps, IspInfo isp) {
    return isp.start() > 0 && isp.duration() > 0 && maxNumberIsps >= isp.end();
  }
}
