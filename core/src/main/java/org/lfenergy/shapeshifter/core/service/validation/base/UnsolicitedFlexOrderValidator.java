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
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@RequiredArgsConstructor
public class UnsolicitedFlexOrderValidator implements UftpValidator<FlexOrder> {

    private static final String REJECTION_REASON = "FlexOfferMessageID must (only) be present if not unsolicited";

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOrder.class == clazz;
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public ValidationResult validate(UftpMessage<FlexOrder> uftpMessage) {
      return ValidationResult.of(isMessageValid(uftpMessage), REJECTION_REASON);
    }

    @Override
    public boolean isValid(UftpMessage<FlexOrder> uftpMessage) {
      return isMessageValid(uftpMessage);
    }

    @Override
    public String getReason() {
      return REJECTION_REASON;
    }

    private boolean isMessageValid(UftpMessage<FlexOrder> uftpMessage) {
        var flexOrder = uftpMessage.payloadMessage();

        return (flexOrder.getFlexOfferMessageID() == null) == (flexOrder.isUnsolicited() != null && flexOrder.isUnsolicited());
    }

}
