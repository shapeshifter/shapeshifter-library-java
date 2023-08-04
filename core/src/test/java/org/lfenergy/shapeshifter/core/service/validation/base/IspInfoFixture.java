package org.lfenergy.shapeshifter.core.service.validation.base;

import org.lfenergy.shapeshifter.api.*;

import java.util.List;

class IspInfoFixture {

    public static MeteringProfileType meteringProfile(IspInfo ispInfo) {
        return meteringProfile(List.of(ispInfo));
    }

    public static MeteringProfileType meteringProfile(List<IspInfo> ispInfos) {
        var meteringProfile = new MeteringProfileType();
        meteringProfile.getISPS().addAll(ispInfos.stream().map(IspInfoFixture::meteringProfileISP).toList());
        return meteringProfile;
    }

    private static MeteringISPType meteringProfileISP(IspInfo ispInfo) {
        var meteringProfileISP = new MeteringISPType();
        meteringProfileISP.setStart(ispInfo.start());
        return meteringProfileISP;
    }

    public static FlexOfferOptionType flexOfferOption(IspInfo ispInfo) {
        return flexOfferOption(List.of(ispInfo));
    }

    public static FlexOfferOptionType flexOfferOption(List<IspInfo> ispInfos) {
        var flexOfferOption = new FlexOfferOptionType();
        flexOfferOption.getISPS().addAll(ispInfos.stream().map(IspInfoFixture::flexOfferOptionISP).toList());
        return flexOfferOption;
    }

    private static FlexOfferOptionISPType flexOfferOptionISP(IspInfo ispInfo) {
        var isp = new FlexOfferOptionISPType();
        isp.setStart(ispInfo.start());
        isp.setDuration(ispInfo.duration());
        return isp;
    }

    public static FlexOrderISPType flexOrderISP(IspInfo ispInfo) {
        var isp = new FlexOrderISPType();
        isp.setStart(ispInfo.start());
        isp.setDuration(ispInfo.duration());
        return isp;
    }

    public static FlexRequestISPType flexRequestISP(IspInfo ispInfo) {
        var isp = new FlexRequestISPType();
        isp.setStart(ispInfo.start());
        isp.setDuration(ispInfo.duration());
        return isp;
    }

    public static DPrognosisISPType dPrognosisISP(IspInfo ispInfo) {
        var isp = new DPrognosisISPType();
        isp.setStart(ispInfo.start());
        isp.setDuration(ispInfo.duration());
        return isp;
    }

    public static FlexReservationUpdateISPType flexReservationUpdateISP(IspInfo ispInfo) {
        var isp = new FlexReservationUpdateISPType();
        isp.setStart(ispInfo.start());
        isp.setDuration(ispInfo.duration());
        return isp;
    }
}
