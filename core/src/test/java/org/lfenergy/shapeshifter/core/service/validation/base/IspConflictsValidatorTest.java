// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.*;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.model.PayloadMessageFixture.DURATION_15_MINUTES;
import static org.lfenergy.shapeshifter.core.model.UftpMessage.createIncoming;
import static org.lfenergy.shapeshifter.core.service.participant.UftpParticipantFixture.DSO;
import static org.lfenergy.shapeshifter.core.service.validation.base.IspInfoFixture.*;

class IspConflictsValidatorTest {

    private static final boolean VALID = true;
    private static final boolean INVALID = false;

    private final IspConflictsValidator testSubject = new IspConflictsValidator();

    @Test
    void getReason() {
        assertThat(testSubject.getReason()).isEqualTo("Overlapping ISPs conflict");
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void isValid(PayloadMessageType msg, List<IspInfo> ispList, boolean expectedResult) {
        setISPs(msg, ispList);

        assertThat(testSubject.isValid(createIncoming(DSO, msg))).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("overlapDueToDuration")
    void isValid_overlapDueToDuration(PayloadMessageType msg, List<IspInfo> ispList, boolean expectedResult) {
        setISPs(msg, ispList);

        assertThat(testSubject.isValid(createIncoming(DSO, msg))).isEqualTo(expectedResult);
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
                // Duplicate starts
                Arguments.of(msg, List.of(
                        new IspInfo(1, 1),
                        new IspInfo(1, 1)
                ), INVALID),
                Arguments.of(msg, List.of(
                        new IspInfo(4, 1),
                        new IspInfo(4, 1)
                ), INVALID),
                Arguments.of(msg, List.of(
                        new IspInfo(20, 1),
                        new IspInfo(20, 1)
                ), INVALID),
                Arguments.of(msg, List.of(
                        new IspInfo(200, 1),
                        new IspInfo(200, 1)
                ), INVALID),

                // No overlap
                Arguments.of(msg, List.of(
                        new IspInfo(1, 10),
                        new IspInfo(11, 10),
                        new IspInfo(21, 10),
                        new IspInfo(31, 10),
                        new IspInfo(41, 10),
                        new IspInfo(51, 10)
                ), VALID),
                Arguments.of(msg, List.of(
                        new IspInfo(1, 10)
                ), VALID),
                Arguments.of(msg, List.of(
                        new IspInfo(20, 10),
                        new IspInfo(30, 10)
                ), VALID),
                Arguments.of(msg, List.of(
                        new IspInfo(1, 1),
                        new IspInfo(20, 10),
                        new IspInfo(150, 10)
                ), VALID)
        ));
    }

    private static Stream<Arguments> overlapDueToDuration() {
        return Stream.of(
                new FlexRequest(),
                new FlexOffer(),
                new FlexOrder(),
                new FlexReservationUpdate(),
                new DPrognosis()
        ).flatMap(msg -> Stream.of(
                // Overlap due to duration
                Arguments.of(msg, List.of(
                        new IspInfo(1, 10),
                        new IspInfo(5, 1)
                ), INVALID),
                Arguments.of(msg, List.of(
                        new IspInfo(1, 10),
                        new IspInfo(10, 1)
                ), INVALID),
                Arguments.of(msg, List.of(
                        new IspInfo(21, 10),
                        new IspInfo(30, 10)
                ), INVALID),
                Arguments.of(msg, List.of(
                        new IspInfo(1, 200),
                        new IspInfo(150, 10)
                ), INVALID)
        ));
    }

    private static void setISPs(PayloadMessageType msg, List<IspInfo> isps) {
        if (msg instanceof FlexMessageType flexMessage) {
            flexMessage.setISPDuration(DURATION_15_MINUTES);

            if (msg instanceof FlexRequest flexRequest) {
                flexRequest.getISPS().clear();
                flexRequest.getISPS().addAll(isps.stream().map(IspInfoFixture::flexRequestISP).toList());
            } else if (msg instanceof FlexOffer flexOffer) {
                flexOffer.getOfferOptions().clear();
                flexOffer.getOfferOptions().add(flexOfferOption(isps));
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
            metering.setISPDuration(DURATION_15_MINUTES);
            metering.getProfiles().clear();
            metering.getProfiles().add(meteringProfile(isps));
        }
    }
}