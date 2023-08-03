// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.lfenergy.shapeshifter.api.*;
import org.lfenergy.shapeshifter.api.datetime.DateTimeCalculation;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.tools.PayloadMessagePropertyRetriever;

import java.util.List;
import java.util.Map;

public abstract class IspListValidatorBase implements UftpValidator<PayloadMessageType> {

    private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
            Map.of(
                    DPrognosis.class, m -> validateIsps((DPrognosis) m),
                    FlexReservationUpdate.class, m -> validateIsps((FlexReservationUpdate) m),
                    FlexRequest.class, m -> validateIsps((FlexRequest) m),
                    FlexOffer.class, m -> validateIsps((FlexOffer) m),
                    FlexOrder.class, m -> validateIsps((FlexOrder) m),
                    // Not validated due to insufficient information
                    // Also might need to check against FlexOrder, but reference and msgId are missing
                    // FlexSettlement.class, (m) -> validateIsps((FlexSettlement) m),
                    Metering.class, m -> validateIsps((Metering) m)
            )
    );

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return retriever.isTypeInMap(clazz);
    }

    @Override
    public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
        return retriever.getProperty(uftpMessage.payloadMessage());
    }

    private long numberOfIspsOnDay(FlexMessageType msg) {
        return DateTimeCalculation.numberOfIspsOnDay(msg.getPeriod(), msg.getISPDuration(), msg.getTimeZone());
    }

    private boolean validateIsps(DPrognosis msg) {
        var maxNumberIsps = numberOfIspsOnDay(msg);
        var isps = msg.getISPS().stream().map(IspInfo::of).toList();
        return validateIsps(maxNumberIsps, isps);
    }

    private boolean validateIsps(FlexReservationUpdate msg) {
        var maxNumberIsps = numberOfIspsOnDay(msg);
        var isps = msg.getISPS().stream().map(IspInfo::of).toList();
        return validateIsps(maxNumberIsps, isps);
    }

    private boolean validateIsps(FlexRequest msg) {
        var maxNumberIsps = numberOfIspsOnDay(msg);
        var isps = msg.getISPS().stream().map(IspInfo::of).toList();
        return validateIsps(maxNumberIsps, isps);
    }

    private boolean validateIsps(FlexOffer msg) {
        var maxNumberIsps = numberOfIspsOnDay(msg);
        return msg.getOfferOptions().stream().allMatch(option -> validateIsps(maxNumberIsps, option));
    }

    private boolean validateIsps(long maxNumberIsps, FlexOfferOptionType option) {
        var isps = option.getISPS().stream().map(IspInfo::of).toList();
        return validateIsps(maxNumberIsps, isps);
    }

    private boolean validateIsps(FlexOrder msg) {
        var maxNumberIsps = numberOfIspsOnDay(msg);
        var isps = msg.getISPS().stream().map(IspInfo::of).toList();
        return validateIsps(maxNumberIsps, isps);
    }

    private boolean validateIsps(Metering msg) {
        var maxNumberIsps = DateTimeCalculation.numberOfIspsOnDay(msg.getPeriod(), msg.getISPDuration(), msg.getTimeZone());
        return msg.getProfiles().stream().allMatch(profile -> validateIsps(maxNumberIsps, profile));
    }

    private boolean validateIsps(long maxNumberIsps, MeteringProfileType profile) {
        var isps = profile.getISPS().stream().map(IspInfo::of).toList();
        return validateIsps(maxNumberIsps, isps);
    }

    protected abstract boolean validateIsps(long maxNumberIsps, List<IspInfo> isps);
}
