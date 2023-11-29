// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;


@RequiredArgsConstructor
public class ReferencedFlexOrderOptionReferenceValidator implements UftpValidator<FlexOrder> {

    private final UftpMessageSupport messageSupport;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOrder.class.equals(clazz);
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public boolean isValid(UftpMessage<FlexOrder> uftpMessage) {
        var flexOrder = uftpMessage.payloadMessage();

        var optionReference = flexOrder.getOptionReference();
        if (optionReference == null || optionReference.isEmpty()) {
            return true;
        }

        return messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(flexOrder.getFlexOfferMessageID(),
                        uftpMessage.payloadMessage().getConversationID(), FlexOffer.class))
                .map(flexOffer -> flexOffer.getOfferOptions()
                        .stream()
                        .anyMatch(option -> optionReference.equals(option.getOptionReference())))
                .orElse(true); // Missing FlexOffer is validated in a separate validator
    }

    @Override
    public String getReason() {
        return "Unknown reference OptionReference in FlexOrder";
    }
}
