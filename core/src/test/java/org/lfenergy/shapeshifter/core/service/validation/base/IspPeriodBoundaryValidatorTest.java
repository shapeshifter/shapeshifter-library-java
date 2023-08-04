// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.model.PayloadMessageFixture.DURATION_15_MINUTES;
import static org.lfenergy.shapeshifter.core.model.UftpMessageFixture.createIncoming;
import static org.lfenergy.shapeshifter.core.service.participant.UftpParticipantFixture.DSO;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.TIME_ZONE_AMSTERDAM;

class IspPeriodBoundaryValidatorTest {

    private static final LocalDate SUMMER_TIME_DAY = LocalDate.parse("2022-03-28");
    private static final LocalDate WINTER_TIME_DAY = LocalDate.parse("2022-10-31");
    private static final LocalDate WINTER_TO_SUMMER_TIME_DAY = LocalDate.parse("2022-03-27");
    private static final LocalDate SUMMER_TO_WINTER_TIME_DAY = LocalDate.parse("2022-10-30");
    private static final boolean VALID = true;
    private static final boolean INVALID = false;

    private final IspPeriodBoundaryValidator testSubject = new IspPeriodBoundaryValidator();

    @Test
    void appliesTo() {
        assertThat(testSubject.appliesTo(DPrognosis.class)).isTrue();
        assertThat(testSubject.appliesTo(FlexReservationUpdate.class)).isTrue();
        assertThat(testSubject.appliesTo(FlexRequest.class)).isTrue();
        assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
        assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
        assertThat(testSubject.appliesTo(Metering.class)).isTrue();
    }

    @Test
    void notAppliesTo() {
        assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
    }

    @Test
    void getReason() {
        assertThat(testSubject.getReason()).isEqualTo("ISPs out of bounds");
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void isValid(PayloadMessageType msg, LocalDate period, long isp1, long isp2, boolean expectedValid) {
        setISPs(msg, period, List.of(new IspInfo(isp1, 1), new IspInfo(isp2, 1)));

        assertThat(testSubject.isValid(createIncoming(DSO, msg)))
                .isEqualTo(expectedValid);
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                new FlexRequest(),
                new FlexOffer(),
                new FlexOrder(),
                new FlexReservationUpdate(),
                new DPrognosis(),
                new Metering()
        ).flatMap(msg -> Stream.of(
                Arguments.of(msg, WINTER_TIME_DAY, 0, 96, INVALID),
                Arguments.of(msg, WINTER_TIME_DAY, 1, 96, VALID),
                Arguments.of(msg, WINTER_TIME_DAY, 1, 97, INVALID),
                Arguments.of(msg, SUMMER_TIME_DAY, 0, 96, INVALID),
                Arguments.of(msg, SUMMER_TIME_DAY, 1, 96, VALID),
                Arguments.of(msg, SUMMER_TIME_DAY, 1, 97, INVALID),
                Arguments.of(msg, WINTER_TO_SUMMER_TIME_DAY, 0, 92, INVALID),
                Arguments.of(msg, WINTER_TO_SUMMER_TIME_DAY, 1, 92, VALID),
                Arguments.of(msg, WINTER_TO_SUMMER_TIME_DAY, 91, 92, VALID),
                Arguments.of(msg, WINTER_TO_SUMMER_TIME_DAY, 92, 93, INVALID),
                Arguments.of(msg, SUMMER_TO_WINTER_TIME_DAY, 0, 97, INVALID),
                Arguments.of(msg, SUMMER_TO_WINTER_TIME_DAY, 1, 97, VALID),
                Arguments.of(msg, SUMMER_TO_WINTER_TIME_DAY, 97, 98, VALID),
                Arguments.of(msg, SUMMER_TO_WINTER_TIME_DAY, 98, 99, VALID),
                Arguments.of(msg, SUMMER_TO_WINTER_TIME_DAY, 99, 100, VALID),
                Arguments.of(msg, SUMMER_TO_WINTER_TIME_DAY, 100, 101, INVALID),
                Arguments.of(msg, SUMMER_TO_WINTER_TIME_DAY, 101, 102, INVALID)
        ));
    }

    private static void setISPs(PayloadMessageType msg, LocalDate period, List<IspInfo> isps) {
        if (msg instanceof FlexMessageType flexMessage) {
            flexMessage.setPeriod(period);
            flexMessage.setISPDuration(DURATION_15_MINUTES);
            flexMessage.setTimeZone(TIME_ZONE_AMSTERDAM);

            if (msg instanceof FlexRequest flexRequest) {
                flexRequest.getISPS().clear();
                flexRequest.getISPS().addAll(isps.stream().map(IspInfoFixture::flexRequestISP).toList());
            } else if (msg instanceof FlexOffer flexOffer) {
                flexOffer.getOfferOptions().clear();
                flexOffer.getOfferOptions().addAll(isps.stream().map(IspInfoFixture::flexOfferOption).toList());
            } else if (msg instanceof FlexOrder flexOrder) {
                flexOrder.getISPS().clear();
                flexOrder.getISPS().addAll(isps.stream().map(IspInfoFixture::flexOrderISP).toList());
            } else if (msg instanceof DPrognosis dPrognosis) {
                dPrognosis.getISPS().clear();
                dPrognosis.getISPS().addAll(isps.stream().map(IspInfoFixture::dPrognosisISP).toList());
            } else if (msg instanceof FlexReservationUpdate flexReservationUpdate) {
                flexReservationUpdate.getISPS().clear();
                flexReservationUpdate.getISPS().addAll(isps.stream().map(IspInfoFixture::flexReservationUpdateISP).toList());
            }
        } else if (msg instanceof Metering metering) {
            metering.setPeriod(period);
            metering.setISPDuration(DURATION_15_MINUTES);
            metering.setTimeZone(TIME_ZONE_AMSTERDAM);
            metering.getProfiles().clear();
            metering.getProfiles().addAll(isps.stream().map(IspInfoFixture::meteringProfile).toList());
        }
    }
}