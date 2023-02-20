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
    return inBounds(maxNumberIsps, isps);
  }

  private boolean inBounds(long maxNumberIsps, List<IspInfo> isps) {
    return isps.stream().allMatch(isp -> inBounds(maxNumberIsps, isp));
  }

  private boolean inBounds(long maxNumberIsps, IspInfo isp) {
    return isp.start() > 0 && isp.duration() > 0 && maxNumberIsps >= isp.end();
  }
}
