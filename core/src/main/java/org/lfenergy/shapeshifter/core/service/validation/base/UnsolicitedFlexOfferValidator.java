// Copyright 2025 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

@RequiredArgsConstructor
public class UnsolicitedFlexOfferValidator implements UftpValidator<FlexOffer> {

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOffer.class == clazz;
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public boolean isValid(UftpMessage<FlexOffer> uftpMessage) {
        var flexOffer = uftpMessage.payloadMessage();

        return flexOffer.isUnsolicited() == null || (flexOffer.getFlexRequestMessageID() == null) == flexOffer.isUnsolicited();
    }

    @Override
    public String getReason() {
        return "FlexRequestMessageID must (only) be present if not unsolicited";
    }
}
