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
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@RequiredArgsConstructor
public class UnsolicitedFlexOfferValidator implements UftpValidator<FlexOffer> {

    private static final String REJECTION_REASON = "FlexRequestMessageID must (only) be present if not unsolicited";

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOffer.class == clazz;
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public ValidationResult validate(UftpMessage<FlexOffer> uftpMessage) {
      return ValidationResult.of(isMessageValid(uftpMessage), REJECTION_REASON);
    }

    private boolean isMessageValid(UftpMessage<FlexOffer> uftpMessage) {
        var flexOffer = uftpMessage.payloadMessage();

        return flexOffer.isUnsolicited() == null || (flexOffer.getFlexRequestMessageID() == null) == flexOffer.isUnsolicited();
    }

}
