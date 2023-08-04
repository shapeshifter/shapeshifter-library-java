// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.lfenergy.shapeshifter.api.*;

import java.util.List;

/**
 * Wrapper class around the specific ISP type of each payload message type, so we can do generic validations.
 */
record IspInfo(long start, long duration) {

    public static IspInfo of(FlexRequestISPType isp) {
        return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(DPrognosisISPType isp) {
        return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(FlexReservationUpdateISPType isp) {
        return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(FlexOfferOptionISPType isp) {
        return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(FlexOrderISPType isp) {
        return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(MeteringISPType isp) {
        return new IspInfo(isp.getStart());
    }

    public static List<IspInfo> fromFlexRequest(FlexRequest flexRequest) {
        return flexRequest.getISPS().stream().map(IspInfo::of).toList();
    }

    public static List<IspInfo> fromFlexOfferOption(FlexOfferOptionType flexOfferOption) {
        return flexOfferOption.getISPS().stream().map(IspInfo::of).toList();
    }

    public static List<IspInfo> fromFlexReservationUpdate(FlexReservationUpdate flexReservationUpdate) {
        return flexReservationUpdate.getISPS().stream().map(IspInfo::of).toList();
    }

    public static List<IspInfo> fromFlexOrder(FlexOrder flexOrder) {
        return flexOrder.getISPS().stream().map(IspInfo::of).toList();
    }

    public static List<IspInfo> fromMeteringProfile(MeteringProfileType meteringProfile) {
        return meteringProfile.getISPS().stream().map(IspInfo::of).toList();
    }

    public static List<IspInfo> fromDPrognosis(DPrognosis dPrognosis) {
        return dPrognosis.getISPS().stream().map(IspInfo::of).toList();
    }

    public IspInfo(Long start) {
        this(start, 1);
    }

    public long end() {
        return start + duration - 1;
    }
}
