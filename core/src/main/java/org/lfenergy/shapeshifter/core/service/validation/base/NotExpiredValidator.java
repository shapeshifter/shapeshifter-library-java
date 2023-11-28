// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

import java.time.OffsetDateTime;
import java.util.Optional;


@RequiredArgsConstructor
public class NotExpiredValidator implements UftpValidator<PayloadMessageType> {

    private final UftpMessageSupport messageSupport;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return FlexOffer.class.equals(clazz) || FlexOrder.class.equals(clazz);
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_FLEX_MESSAGE;
    }

    @Override
    public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
        var payloadMessage = uftpMessage.payloadMessage();

        if (payloadMessage instanceof FlexOffer flexOffer) {
            return validateFlexRequestNotExpired(uftpMessage, flexOffer);
        }
        if (payloadMessage instanceof FlexOrder flexOrder) {
            return validateFlexOfferNotExpired(uftpMessage, flexOrder);
        }

        return true;
    }

    @Override
    public String getReason() {
        return "Reference message expired";
    }

    private boolean validateFlexRequestNotExpired(UftpMessage<PayloadMessageType> uftpMessage, FlexOffer msg) {
        var flexRequestMessageID = Optional.ofNullable(msg.getFlexRequestMessageID());
        if (flexRequestMessageID.isEmpty()) {
            return true;
        }

        var request = messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(flexRequestMessageID.get(), msg.getConversationID(), FlexRequest.class));
        return request.map(flexRequest -> validate(flexRequest.getExpirationDateTime())).orElse(true);
    }

    private boolean validateFlexOfferNotExpired(UftpMessage<PayloadMessageType> uftpMessage, FlexOrder msg) {
        var messageId = Optional.ofNullable(msg.getFlexOfferMessageID());
        if (messageId.isEmpty()) {
            return true;
        }

        var offer = messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(messageId.get(), msg.getConversationID(), FlexOffer.class));
        return offer.map(flexOffer -> validate(flexOffer.getExpirationDateTime())).orElse(true);
    }

    private boolean validate(OffsetDateTime expirationDateTime) {
        var now = OffsetDateTime.now();
        return now.isBefore(expirationDateTime);
    }
}
