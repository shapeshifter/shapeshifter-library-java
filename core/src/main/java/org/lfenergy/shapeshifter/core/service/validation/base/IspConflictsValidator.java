// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.*;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;


@RequiredArgsConstructor
public class IspConflictsValidator implements UftpValidator<PayloadMessageType> {

    public static final Set<Class<? extends PayloadMessageType>> APPLIES_TO = Set.of(
            DPrognosis.class,
            FlexReservationUpdate.class,
            FlexRequest.class,
            FlexOffer.class,
            FlexOrder.class,
            // Not validated due to insufficient information
            // Also might need to check against FlexOrder, but reference and msgId are missing
            // FlexSettlement.class,
            Metering.class);

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return APPLIES_TO.contains(clazz);
    }

    @Override
    public int order() {
        return ValidationOrder.SPEC_FLEX_MESSAGE;
    }

    @Override
    public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
        var payloadMessage = uftpMessage.payloadMessage();

        if (payloadMessage instanceof FlexMessageType flexMessage) {
            return validateFlexMessage(flexMessage);
        } else if (payloadMessage instanceof Metering metering) {
            return validateMetering(metering);
        }

        return false;
    }

    @Override
    public String getReason() {
        return "Overlapping ISPs conflict";
    }

    private boolean validateFlexMessage(FlexMessageType flexMessage) {
        if (flexMessage instanceof FlexRequest flexRequest) {
            return hasNoOverlap(IspInfo.fromFlexRequest(flexRequest));
        } else if (flexMessage instanceof FlexOffer flexOffer) {
            return flexOffer.getOfferOptions().stream().allMatch(offerOption -> hasNoOverlap(IspInfo.fromFlexOfferOption(offerOption)));
        } else if (flexMessage instanceof FlexOrder flexOrder) {
            return hasNoOverlap(IspInfo.fromFlexOrder(flexOrder));
        } else if (flexMessage instanceof FlexReservationUpdate flexReservationUpdate) {
            return hasNoOverlap(IspInfo.fromFlexReservationUpdate(flexReservationUpdate));
        } else if (flexMessage instanceof DPrognosis dPrognosis) {
            return hasNoOverlap(IspInfo.fromDPrognosis(dPrognosis));
        }

        return false;
    }

    private boolean validateMetering(Metering metering) {
        return metering.getProfiles().stream().allMatch(profile -> hasNoOverlap(IspInfo.fromMeteringProfile(profile)));
    }

    private boolean hasNoOverlap(List<IspInfo> isps) {
        Set<Long> used = new HashSet<>();
        return isps.stream().allMatch(isp -> hasNoOverlap(used, isp));
    }

    private boolean hasNoOverlap(Set<Long> used, IspInfo isp) {
        var thisRange = LongStream.range(isp.start(), isp.end() + 1/*exclusive*/).boxed().toList();
        var result = Collections.disjoint(used, thisRange);
        used.addAll(thisRange);
        return result;
    }
}
