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
 * Validates that each flex request has at least one ISP with disposition REQUESTED
 */
public class IspRequestedDispositionValidator implements UftpValidator<FlexRequest> {

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
    return flexRequest.getISPS().stream().anyMatch(this::dispositionEqualsRequested);
  }

  private boolean dispositionEqualsRequested(FlexRequestISPType isp) {
    return isp.getDisposition().equals(AvailableRequestedType.REQUESTED);
  }

  @Override
  public String getReason() {
    return "Lacking Requested disposition";
  }
}
