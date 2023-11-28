// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.FlexSettlementResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;


@RequiredArgsConstructor
public class ReferencedFlexSettlementReferenceValidator implements UftpValidator<FlexSettlementResponse> {

    private final UftpMessageSupport messageSupport;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexSettlementResponse.class.isAssignableFrom(clazz);
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public boolean isValid(UftpMessage<FlexSettlementResponse> uftpMessage) {
        var flexSettlementResponse = uftpMessage.payloadMessage();
        return messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(flexSettlementResponse.getFlexSettlementMessageID(),
                uftpMessage.payloadMessage().getConversationID(),
                FlexSettlement.class)).isPresent();
    }

    @Override
    public String getReason() {
        return "Flex Settlement Response refers to unknown Flex Settlement";
    }
}
