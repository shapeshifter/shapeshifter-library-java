package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IspConflictsValidator extends IspListValidatorBase {


  @Override
  public String getReason() {
    return "ISP conflict";
  }

  @Override
  protected boolean validateIsps(long maxNumberIsps, List<IspInfo> isps) {
    return noOverlap(isps);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  private boolean noOverlap(List<IspInfo> isps) {
    Set<Long> used = new HashSet<>();
    return isps.stream().allMatch(isp -> noOverlap(used, isp));
  }

  private boolean noOverlap(Set<Long> used, IspInfo isp) {
    var thisRange = LongStream.range(isp.start(), isp.end() + 1/*exclusive*/).boxed().toList();
    var result = Collections.disjoint(used, thisRange);
    used.addAll(thisRange);
    return result;
  }
}
