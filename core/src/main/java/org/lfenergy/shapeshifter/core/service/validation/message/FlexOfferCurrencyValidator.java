// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

import java.util.Currency;

/**
 * Validates that the currency of a FlexOffer is a known ISO 4217 currency.
 */
public class FlexOfferCurrencyValidator implements UftpValidator<FlexOffer> {

    static final int ORDER = ValidationOrder.SPEC_MESSAGE_SPECIFIC;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return clazz.equals(FlexOffer.class);
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public boolean isValid(UftpMessage<FlexOffer> uftpMessage) {
        var flexOffer = uftpMessage.payloadMessage();

        if (flexOffer.getCurrency() == null || flexOffer.getCurrency().isBlank()) {
            return false;
        }

        try {
            Currency.getInstance(flexOffer.getCurrency());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getReason() {
        return "Currency in FlexOffer is not a known ISO 4217 currency";
    }
}
