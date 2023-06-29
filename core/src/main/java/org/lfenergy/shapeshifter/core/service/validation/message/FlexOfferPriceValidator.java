// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Validates that the price of the offer is a valid price for the given currency.
 */
@RequiredArgsConstructor
public class FlexOfferPriceValidator implements UftpValidator<FlexOffer> {

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return clazz.equals(FlexOffer.class);
    }

    @Override
    public int order() {
        // For a better rejection reason, it's best to first validate the currency and then the price
        return FlexOfferCurrencyValidator.ORDER + 1;
    }

    @Override
    public boolean isValid(UftpMessage<FlexOffer> uftpMessage) {
        var flexOffer = uftpMessage.payloadMessage();

        if (flexOffer.getCurrency() == null || flexOffer.getCurrency().isBlank()) {
            return false;
        }

        Currency currency;
        try {
            currency = Currency.getInstance(flexOffer.getCurrency());
        } catch (IllegalArgumentException e) {
            // Currency is not known
            return false;
        }

        return flexOffer.getOfferOptions().stream()
                .filter(offerOption -> offerOption.getPrice() != null)
                .allMatch(offerOption -> offerOption.getPrice().scale() == currency.getDefaultFractionDigits());
    }

    @Override
    public String getReason() {
        return "Price in FlexOffer option is not valid for the given currency";
    }
}
