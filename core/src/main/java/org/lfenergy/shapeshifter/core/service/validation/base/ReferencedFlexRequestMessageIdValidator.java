// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

import java.util.Optional;

@RequiredArgsConstructor
public class ReferencedFlexRequestMessageIdValidator implements UftpValidator<FlexOffer> {

    private static final String REJECTION_REASON = "Unknown reference FlexRequestMessageID";

    private final UftpMessageSupport messageSupport;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOffer.class.isAssignableFrom(clazz);
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
        var flexRequestMessageID = Optional.ofNullable(uftpMessage.payloadMessage().getFlexRequestMessageID());
        return flexRequestMessageID.isEmpty() || messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(flexRequestMessageID.get(),
                uftpMessage.payloadMessage().getConversationID(), FlexRequest.class)).isPresent();
    }

}
