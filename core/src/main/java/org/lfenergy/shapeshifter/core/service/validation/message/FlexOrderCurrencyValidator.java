// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

import java.util.Currency;

/**
 * Validates that the currency of a FlexOrder is a known ISO 4217 currency.
 */
@RequiredArgsConstructor
public class FlexOrderCurrencyValidator implements UftpValidator<FlexOrder> {

  private static final String REJECTION_REASON = "Currency in FlexOrder is not a known ISO 4217 currency";

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOrder.class);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public ValidationResult validate(UftpMessage<FlexOrder> uftpMessage) {
    return ValidationResult.of(isMessageValid(uftpMessage), REJECTION_REASON);
  }

  private boolean isMessageValid(UftpMessage<FlexOrder> uftpMessage) {
    var flexOrder = uftpMessage.payloadMessage();

    if (flexOrder.getCurrency() == null || flexOrder.getCurrency().isBlank()) {
      return false;
    }

    try {
      Currency.getInstance(flexOrder.getCurrency());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

}
