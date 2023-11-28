// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

import java.math.BigDecimal;

/**
 * Validates that at the price of the order matches the price in the offer
 */


@RequiredArgsConstructor
public class FlexOrderPriceMatchValidator implements UftpValidator<FlexOrder> {

    private final UftpMessageSupport messageSupport;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return clazz.equals(FlexOrder.class);
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public boolean isValid(UftpMessage<FlexOrder> uftpMessage) {
        var flexOrder = uftpMessage.payloadMessage();
        var flexOffer = messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(flexOrder.getFlexOfferMessageID(),
                flexOrder.getConversationID(), FlexOffer.class));
        return flexOffer.map(offer -> offer.getOfferOptions().stream().allMatch(it -> priceMatches(it, flexOrder.getPrice()))).orElse(false);
    }

    private boolean priceMatches(FlexOfferOptionType flexOfferOption, BigDecimal orderPrice) {
        return flexOfferOption.getPrice().compareTo(orderPrice) == 0;
    }

    @Override
    public String getReason() {
        return "Price in the order does not match the price given in the offer";
    }
}
