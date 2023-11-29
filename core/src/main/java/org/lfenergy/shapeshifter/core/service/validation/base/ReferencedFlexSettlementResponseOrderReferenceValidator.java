// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementStatusType;
import org.lfenergy.shapeshifter.api.FlexSettlementResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

import java.util.Set;

import static org.lfenergy.shapeshifter.core.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;


@RequiredArgsConstructor
public class ReferencedFlexSettlementResponseOrderReferenceValidator implements UftpValidator<FlexSettlementResponse> {

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
        var orderReferences = collectOrderReferences(flexSettlementResponse);
        return orderReferences.isEmpty() || orderReferences.stream().allMatch(
                orderReference -> messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(orderReference, flexSettlementResponse.getConversationID(),
                        FlexOrder.class)).isPresent());
    }

    @Override
    public String getReason() {
        return "Unknown reference OrderReference in FlexSettlementResponse";
    }

    private Set<String> collectOrderReferences(FlexSettlementResponse m) {
        return m.getFlexOrderSettlementStatuses().stream()
                .map(FlexOrderSettlementStatusType::getOrderReference)
                .collect(toSetIgnoreNulls());
    }
}
