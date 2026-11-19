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
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@RequiredArgsConstructor
public class FlexOrderOfferIsNotRevokedValidator implements UftpValidator<FlexOrder> {

    private static final String REJECTION_REASON = "Reference message revoked";

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
        var msg = uftpMessage.payloadMessage();
        return messageSupport.findFlexRevocation(msg.getConversationID(),
                msg.getFlexOfferMessageID(), msg.getSenderDomain(), msg.getRecipientDomain()).isEmpty();
    }

}
