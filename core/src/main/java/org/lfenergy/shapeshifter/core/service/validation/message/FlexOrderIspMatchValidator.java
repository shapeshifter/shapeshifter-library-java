// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderISPType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

import java.util.List;

/**
 * Validates that ISPs in the flex order match the ISPs in the flex offer
 */
@RequiredArgsConstructor
public class FlexOrderIspMatchValidator implements UftpValidator<FlexOrder> {

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
        // if there is no flex offer => return false
        if (flexOffer.isEmpty()) {
            return false;
        }
        var allIspsInFlexOffers = getAllOfferOptionISPs(flexOffer.get());

        var allOrderIspMatchOfferIsp = flexOrder.getISPS().stream().allMatch(it -> ispAppearsInFlexOffer(it, allIspsInFlexOffers));
        // All ISPs in order match an ISP in the offer; do an extra check to verify that there are
        // no extra ISP in the offer that are not in the order.... Just do this by checking the number of ISP's in
        // both order and offer
        var equalAmountOrderIspAndOfferIsp = flexOrder.getISPS().size() == allIspsInFlexOffers.size();
        return allOrderIspMatchOfferIsp && equalAmountOrderIspAndOfferIsp;
    }

    private boolean ispAppearsInFlexOffer(FlexOrderISPType ispInOrder, List<FlexOfferOptionISPType> ispsInOffer) {
        return ispsInOffer.stream().anyMatch(it -> match(ispInOrder, it));
    }

    private boolean match(FlexOrderISPType ispInOrder, FlexOfferOptionISPType ispInOffer) {
        return ispInOrder.getStart().longValue() == ispInOffer.getStart().longValue() &&
                ispInOrder.getDuration() == ispInOffer.getDuration();
    }

    @Override
    public String getReason() {
        return "ISPs from the FlexOrder do not match the ISPs given in the FlexOffer";
    }

    private List<FlexOfferOptionISPType> getAllOfferOptionISPs(FlexOffer flexOffer) {
        return flexOffer.getOfferOptions().stream().map(FlexOfferOptionType::getISPS).flatMap(List::stream).toList();
    }
}