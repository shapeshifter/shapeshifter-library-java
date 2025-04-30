// Copyright 2025 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;


@RequiredArgsConstructor
public class UnsolicitedFlexOrderValidator implements UftpValidator<FlexOrder> {

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOrder.class == clazz;
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public boolean isValid(UftpMessage<FlexOrder> uftpMessage) {
        var flexOrder = uftpMessage.payloadMessage();

        return flexOrder.isUnsolicited() == null || (flexOrder.getFlexOfferMessageID() == null) == flexOrder.isUnsolicited();
    }

    @Override
    public String getReason() {
        return "FlexOfferMessageID must (only) be present if not unsolicited";
    }
}
