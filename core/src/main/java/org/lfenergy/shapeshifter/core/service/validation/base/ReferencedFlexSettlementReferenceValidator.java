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
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@RequiredArgsConstructor
public class ReferencedFlexSettlementReferenceValidator implements UftpValidator<FlexSettlementResponse> {

    private static final String REJECTION_REASON = "Flex Settlement Response refers to unknown Flex Settlement";

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
    public ValidationResult validate(UftpMessage<FlexSettlementResponse> uftpMessage) {
      return ValidationResult.of(isMessageValid(uftpMessage), REJECTION_REASON);
    }

    private boolean isMessageValid(UftpMessage<FlexSettlementResponse> uftpMessage) {
        var flexSettlementResponse = uftpMessage.payloadMessage();
        return messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(flexSettlementResponse.getFlexSettlementMessageID(),
                uftpMessage.payloadMessage().getConversationID(),
                FlexSettlement.class)).isPresent();
    }

}
