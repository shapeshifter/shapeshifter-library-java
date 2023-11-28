// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.DPrognosisResponse;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderStatusType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

import java.util.Set;

import static org.lfenergy.shapeshifter.core.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;


@RequiredArgsConstructor
public class ReferencedFlexOrderMessageIdValidator implements UftpValidator<DPrognosisResponse> {

    private final UftpMessageSupport messageSupport;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return DPrognosisResponse.class.isAssignableFrom(clazz);
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
    }

    @Override
    public boolean isValid(UftpMessage<DPrognosisResponse> uftpMessage) {
        var value = collectFlexOrderMessageIDs(uftpMessage.payloadMessage());
        return value.isEmpty() || value.stream().allMatch(
                msgId -> messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(msgId,
                        uftpMessage.payloadMessage().getConversationID(), FlexOrder.class)).isPresent()
        );
    }

    @Override
    public String getReason() {
        return "Unknown reference FlexOrderMessageID";
    }

    private Set<String> collectFlexOrderMessageIDs(DPrognosisResponse m) {
        return m.getFlexOrderStatuses().stream()
                .map(FlexOrderStatusType::getFlexOrderMessageID)
                .collect(toSetIgnoreNulls());
    }
}
