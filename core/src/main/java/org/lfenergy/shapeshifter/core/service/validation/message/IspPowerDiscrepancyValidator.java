// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

/**
 * Validates that all ISPs that have disposition = requested indicate a power direction, thus having either a minPower &lt; 0 *or* a maxPower &gt; 0 but not both.
 */
public class IspPowerDiscrepancyValidator implements UftpValidator<FlexRequest> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexRequest.class);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean isValid(UftpMessage<FlexRequest> uftpMessage) {
    var flexRequest = uftpMessage.payloadMessage();
    return flexRequest.getISPS().stream().filter(this::dispositionEqualsRequested).noneMatch(this::powerHasNoDirection);
  }

  private boolean dispositionEqualsRequested(FlexRequestISPType isp) {
    return isp.getDisposition().equals(AvailableRequestedType.REQUESTED);
  }

  // We say that power has no direction if both min power < 0 and max power > 0; in that case
  // we have a flex request message that suggests that it's both REQUESTED and AVAILABLE
  // at the same time....
  private boolean powerHasNoDirection(FlexRequestISPType isp) {
    return isp.getMinPower() < 0 && isp.getMaxPower() > 0;
  }

  @Override
  public String getReason() {
    return "One or more ISPs with a 'Requested' disposition has no direction";
  }
}
