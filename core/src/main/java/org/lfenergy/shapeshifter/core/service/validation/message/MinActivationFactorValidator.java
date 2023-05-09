// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import java.math.BigDecimal;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

/**
 * Validates that the min activation factor (if present) is in the interval <0,1] (thus: > 0, <=1)
 */


public class MinActivationFactorValidator implements UftpValidator<FlexOffer> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOffer.class);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean isValid(UftpMessage<FlexOffer> uftpMessage) {
    var flexOffer = uftpMessage.payloadMessage();
    return flexOffer.getOfferOptions().stream().allMatch(
        it -> minActivationFactorIsNull(it.getMinActivationFactor()) || minActivationFactorInProperRange(it.getMinActivationFactor()));
  }

  private boolean minActivationFactorIsNull(BigDecimal b) {
    return b == null;
  }

  private boolean minActivationFactorInProperRange(BigDecimal b) {
    return b.doubleValue() > 0.0 && b.doubleValue() <= 1.0;
  }

  @Override
  public String getReason() {
    return "Min activation factor must be between 0 and 1 (inclusive)";
  }
}
