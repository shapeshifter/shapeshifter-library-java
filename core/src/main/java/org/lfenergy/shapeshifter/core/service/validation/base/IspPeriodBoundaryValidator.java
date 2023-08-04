// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.lfenergy.shapeshifter.api.*;
import org.lfenergy.shapeshifter.api.datetime.DateTimeCalculation;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

import java.util.List;
import java.util.Set;

public class IspPeriodBoundaryValidator implements UftpValidator<PayloadMessageType> {

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
        return "ISPs out of bounds";
    }

    private boolean validateFlexMessage(FlexMessageType flexMessage) {
        var maxNumberIsps = DateTimeCalculation.numberOfIspsOnDay(flexMessage.getPeriod(), flexMessage.getISPDuration(), flexMessage.getTimeZone());

        if (flexMessage instanceof FlexRequest flexRequest) {
            return isInBounds(maxNumberIsps, IspInfo.fromFlexRequest(flexRequest));
        } else if (flexMessage instanceof FlexOffer flexOffer) {
            return flexOffer.getOfferOptions().stream().allMatch(offerOption -> isInBounds(maxNumberIsps, IspInfo.fromFlexOfferOption(offerOption)));
        } else if (flexMessage instanceof FlexOrder flexOrder) {
            return isInBounds(maxNumberIsps, IspInfo.fromFlexOrder(flexOrder));
        } else if (flexMessage instanceof FlexReservationUpdate flexReservationUpdate) {
            return isInBounds(maxNumberIsps, IspInfo.fromFlexReservationUpdate(flexReservationUpdate));
        } else if (flexMessage instanceof DPrognosis dPrognosis) {
            return isInBounds(maxNumberIsps, IspInfo.fromDPrognosis(dPrognosis));
        }

        return false;
    }

    private boolean validateMetering(Metering metering) {
        var maxNumberIsps = DateTimeCalculation.numberOfIspsOnDay(metering.getPeriod(), metering.getISPDuration(), metering.getTimeZone());

        return metering.getProfiles().stream().allMatch(profile -> isInBounds(maxNumberIsps, IspInfo.fromMeteringProfile(profile)));
    }

    private boolean isInBounds(long maxNumberIsps, List<IspInfo> isps) {
        return isps.stream().allMatch(isp -> isInBounds(maxNumberIsps, isp));
    }

    private boolean isInBounds(long maxNumberIsps, IspInfo isp) {
        return isp.start() > 0 && maxNumberIsps >= isp.end();
    }
}
