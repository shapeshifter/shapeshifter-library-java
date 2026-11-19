// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferRevocation;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@RequiredArgsConstructor
public class FlexOfferRevocationSenderDomainValidator implements UftpValidator<FlexOfferRevocation> {

    private static final String REJECTION_REASON = "Flex Offer revocation can only be sent by the same Sender Domain that sent the Flex Offer";
    private UftpMessageSupport uftpMessageSupport;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOfferRevocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public ValidationResult validate(UftpMessage<FlexOfferRevocation> uftpMessage) {
      return ValidationResult.of(isMessageValid(uftpMessage), REJECTION_REASON);
    }

    private boolean isMessageValid(UftpMessage<FlexOfferRevocation> uftpMessage) {
        var msg = uftpMessage.payloadMessage();
        var originalFlexOffer = uftpMessageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(msg.getConversationID(),
                msg.getFlexOfferMessageID(), FlexOffer.class));
        return originalFlexOffer.isPresent() && msg.getSenderDomain().equals(originalFlexOffer.get().getSenderDomain());
    }

}
