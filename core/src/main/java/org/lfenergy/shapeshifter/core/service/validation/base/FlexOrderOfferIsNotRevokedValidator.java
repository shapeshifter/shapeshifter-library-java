// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

@RequiredArgsConstructor
public class FlexOrderOfferIsNotRevokedValidator implements UftpValidator<FlexOrder> {

    private final UftpMessageSupport messageSupport;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOrder.class.isAssignableFrom(clazz);
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public boolean isValid(UftpMessage<FlexOrder> uftpMessage) {
        var msg = uftpMessage.payloadMessage();
        return messageSupport.findFlexRevocation(msg.getConversationID(),
                msg.getFlexOfferMessageID(), msg.getSenderDomain(), msg.getRecipientDomain()).isEmpty();
    }

    @Override
    public String getReason() {
        return "Reference message revoked";
    }
}
