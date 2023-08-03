// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.*;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IspListValidatorBaseTest {

    private static final LocalDate WINTER_TIME_DAY = LocalDate.parse("2022-11-22");
    private static final LocalDate WINTER_TO_SUMMER_TIME_DAY = LocalDate.parse("2022-03-27");
    private static final LocalDate SUMMER_TO_WINTER_TIME_DAY = LocalDate.parse("2022-10-30");

    private static final Duration DURATION_15_MINUTES = Duration.ofMinutes(15);
    private static final String TIME_ZONE_AMSTERDAM = "Europe/Amsterdam";

    @Mock
    private UftpParticipant sender;

    @Mock
    private List<IspInfo> listIsps;

    @Captor
    private ArgumentCaptor<List<IspInfo>> actualIsps;

    private IspListValidatorBase testSubject;

    @BeforeEach
    void setUp() {
        testSubject = mock(IspListValidatorBase.class, Mockito.withSettings()
                .useConstructor()
                .defaultAnswer(Mockito.CALLS_REAL_METHODS));
    }

    @AfterEach
    void noMore() {
        verifyNoMoreInteractions(
                sender,
                listIsps
        );
    }

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

    public static Stream<Arguments> withParameters() {
        DPrognosis dPrognosis = new DPrognosis();
        dPrognosis.setPeriod(WINTER_TIME_DAY);
        dPrognosis.setISPDuration(DURATION_15_MINUTES);
        dPrognosis.setTimeZone(TIME_ZONE_AMSTERDAM);
        dPrognosis.getISPS().addAll(List.of(
                setStartAndDuration(new DPrognosisISPType(), 1, 1),
                setStartAndDuration(new DPrognosisISPType(), 2, 2)
        ));

        FlexReservationUpdate flexReservationUpdate = new FlexReservationUpdate();
        flexReservationUpdate.setPeriod(WINTER_TIME_DAY);
        flexReservationUpdate.setISPDuration(DURATION_15_MINUTES);
        flexReservationUpdate.setTimeZone(TIME_ZONE_AMSTERDAM);
        // No ISPs (not allowed bij XSD)

        FlexRequest flexRequest = new FlexRequest();
        flexRequest.setPeriod(WINTER_TIME_DAY);
        flexRequest.setISPDuration(DURATION_15_MINUTES);
        flexRequest.setTimeZone(TIME_ZONE_AMSTERDAM);
        flexRequest.getISPS().addAll(List.of(
                setStartAndDuration(new FlexRequestISPType(), 1, 1),
                setStartAndDuration(new FlexRequestISPType(), 2, 2)
        ));

        FlexOfferOptionType flexOffer1 = new FlexOfferOptionType();
        flexOffer1.getISPS().addAll(List.of(
                setStartAndDuration(new FlexOfferOptionISPType(), 1, 1),
                setStartAndDuration(new FlexOfferOptionISPType(), 2, 2)
        ));

        FlexOfferOptionType flexOffer2 = new FlexOfferOptionType();
        flexOffer2.getISPS().addAll(List.of(
                setStartAndDuration(new FlexOfferOptionISPType(), 3, 3),
                setStartAndDuration(new FlexOfferOptionISPType(), 4, 4)
        ));

        FlexOffer flexOffer = new FlexOffer();
        flexOffer.setPeriod(WINTER_TIME_DAY);
        flexOffer.setISPDuration(DURATION_15_MINUTES);
        flexOffer.setTimeZone(TIME_ZONE_AMSTERDAM);
        flexOffer.getOfferOptions().addAll(List.of(flexOffer1, flexOffer2));

        FlexOrder flexOrder = new FlexOrder();
        flexOrder.setPeriod(WINTER_TIME_DAY);
        flexOrder.setISPDuration(DURATION_15_MINUTES);
        flexOrder.setTimeZone(TIME_ZONE_AMSTERDAM);
        flexOrder.getISPS().addAll(List.of(
                setStartAndDuration(new FlexOrderISPType(), 1, 1),
                setStartAndDuration(new FlexOrderISPType(), 2, 2)
        ));

        MeteringProfileType meteringProfile1 = new MeteringProfileType();
        meteringProfile1.getISPS().addAll(List.of(
                setStart(new MeteringISPType(), 1),
                setStart(new MeteringISPType(), 2)
        ));

        MeteringProfileType meteringProfile2 = new MeteringProfileType();
        meteringProfile2.getISPS().addAll(List.of(
                setStart(new MeteringISPType(), 3),
                setStart(new MeteringISPType(), 4)
        ));

        Metering meteringMessage = new Metering();
        meteringMessage.setPeriod(WINTER_TIME_DAY);
        meteringMessage.setISPDuration(DURATION_15_MINUTES);
        meteringMessage.setTimeZone(TIME_ZONE_AMSTERDAM);
        meteringMessage.getProfiles().addAll(List.of(meteringProfile1, meteringProfile2));

        var flexRequestOnWinterToSummerTimeDay = new FlexRequest();
        flexRequestOnWinterToSummerTimeDay.setPeriod(WINTER_TO_SUMMER_TIME_DAY);
        flexRequestOnWinterToSummerTimeDay.setISPDuration(DURATION_15_MINUTES);
        flexRequestOnWinterToSummerTimeDay.setTimeZone(TIME_ZONE_AMSTERDAM);
        flexRequestOnWinterToSummerTimeDay.getISPS().addAll(List.of(
                setStartAndDuration(new FlexRequestISPType(), 1, 1),
                setStartAndDuration(new FlexRequestISPType(), 2, 2)
        ));

        var flexRequestOnSummerToWinterTimeDay = new FlexRequest();
        flexRequestOnSummerToWinterTimeDay.setPeriod(SUMMER_TO_WINTER_TIME_DAY);
        flexRequestOnSummerToWinterTimeDay.setISPDuration(DURATION_15_MINUTES);
        flexRequestOnSummerToWinterTimeDay.setTimeZone(TIME_ZONE_AMSTERDAM);
        flexRequestOnSummerToWinterTimeDay.getISPS().addAll(List.of(
                setStartAndDuration(new FlexRequestISPType(), 1, 1),
                setStartAndDuration(new FlexRequestISPType(), 2, 2)
        ));

        return Stream.of(
                Arguments.of(dPrognosis, List.of(
                        // One call to abstract method validateIsps
                        List.of(new IspInfo(1, 1), new IspInfo(2, 2))
                ), 96),
                Arguments.of(flexReservationUpdate, List.of(
                        // One call to abstract method validateIsps with empty list
                        List.of()
                ), 96),
                Arguments.of(flexRequest, List.of(
                        // One call to abstract method validateIsps
                        List.of(new IspInfo(1, 1), new IspInfo(2, 2))
                ), 96),
                Arguments.of(flexOffer, List.of(
                        // Two calls to abstract method validateIsps
                        List.of(new IspInfo(1, 1), new IspInfo(2, 2)),
                        List.of(new IspInfo(3, 3), new IspInfo(4, 4))
                ), 96),
                Arguments.of(flexOrder, List.of(
                        // One call to abstract method validateIsps
                        List.of(new IspInfo(1, 1), new IspInfo(2, 2))
                ), 96),
                Arguments.of(meteringMessage, List.of(
                        // Two calls to abstract method validateIsps
                        List.of(new IspInfo(1, 1), new IspInfo(2, 1)),
                        List.of(new IspInfo(3, 1), new IspInfo(4, 1))
                ), 96),
                Arguments.of(flexRequestOnWinterToSummerTimeDay, List.of(
                        // One call to abstract method validateIsps
                        List.of(new IspInfo(1, 1), new IspInfo(2, 2))
                ), 92),
                Arguments.of(flexRequestOnSummerToWinterTimeDay, List.of(
                        // One call to abstract method validateIsps
                        List.of(new IspInfo(1, 1), new IspInfo(2, 2))
                ), 100)
        );
    }

    public static Stream<Arguments> withInvalidParameters() {
        var invalidFlexRequestOnWinterToSummerTimeDay = new FlexRequest();
        invalidFlexRequestOnWinterToSummerTimeDay.setPeriod(WINTER_TO_SUMMER_TIME_DAY);
        invalidFlexRequestOnWinterToSummerTimeDay.setISPDuration(DURATION_15_MINUTES);
        invalidFlexRequestOnWinterToSummerTimeDay.setTimeZone(TIME_ZONE_AMSTERDAM);
        invalidFlexRequestOnWinterToSummerTimeDay.getISPS().addAll(List.of(
                setStartAndDuration(new FlexRequestISPType(), 93, 1),
                setStartAndDuration(new FlexRequestISPType(), 94, 2)
        ));


        var invalidFlexRequestOnSummerToWinterTimeDay = new FlexRequest();
        invalidFlexRequestOnSummerToWinterTimeDay.setPeriod(SUMMER_TO_WINTER_TIME_DAY);
        invalidFlexRequestOnSummerToWinterTimeDay.setISPDuration(DURATION_15_MINUTES);
        invalidFlexRequestOnSummerToWinterTimeDay.setTimeZone(TIME_ZONE_AMSTERDAM);
        invalidFlexRequestOnSummerToWinterTimeDay.getISPS().addAll(List.of(
                setStartAndDuration(new FlexRequestISPType(), 101, 1),
                setStartAndDuration(new FlexRequestISPType(), 102, 2)
        ));

        return Stream.of(
                Arguments.of(invalidFlexRequestOnWinterToSummerTimeDay, List.of(
                        List.of(new IspInfo(93, 1), new IspInfo(94, 2))
                ), 92),
                Arguments.of(invalidFlexRequestOnSummerToWinterTimeDay, List.of(
                        List.of(new IspInfo(101, 1), new IspInfo(102, 2))
                ), 100)
        );
    }

    @ParameterizedTest
    @MethodSource("withInvalidParameters")
    void invalid(PayloadMessageType msg, List<List<IspInfo>> expectedIspsInCalls, long expectedMaxIsps) {
        when(testSubject.validateIsps(anyLong(), anyList())).thenReturn(false);

        assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, msg))).isFalse();

        verify(testSubject, times(expectedIspsInCalls.size())).validateIsps(eq(expectedMaxIsps), actualIsps.capture());

        assertThat(actualIsps.getAllValues()).containsExactlyElementsOf(expectedIspsInCalls);
    }

    private static DPrognosisISPType setStartAndDuration(DPrognosisISPType instance, long start, long duration) {
        instance.setStart(start);
        instance.setDuration(duration);
        return instance;
    }

    private static FlexRequestISPType setStartAndDuration(FlexRequestISPType instance, long start, long duration) {
        instance.setStart(start);
        instance.setDuration(duration);
        return instance;
    }

    private static FlexOfferOptionISPType setStartAndDuration(FlexOfferOptionISPType instance, long start, long duration) {
        instance.setStart(start);
        instance.setDuration(duration);
        return instance;
    }

    private static FlexOrderISPType setStartAndDuration(FlexOrderISPType instance, long start, long duration) {
        instance.setStart(start);
        instance.setDuration(duration);
        return instance;
    }

    private static MeteringISPType setStart(MeteringISPType instance, long start) {
        instance.setStart(start);
        return instance;
    }

    @ParameterizedTest
    @MethodSource("withParameters")
    void valid(PayloadMessageType msg, List<List<IspInfo>> expectedIspsInCalls, long expectedMaxIsps) {
        when(testSubject.validateIsps(anyLong(), anyList())).thenReturn(true);

        assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, msg))).isTrue();

        verify(testSubject, times(expectedIspsInCalls.size())).validateIsps(eq(expectedMaxIsps), actualIsps.capture());

        assertThat(actualIsps.getAllValues()).containsExactlyElementsOf(expectedIspsInCalls);
    }
}
