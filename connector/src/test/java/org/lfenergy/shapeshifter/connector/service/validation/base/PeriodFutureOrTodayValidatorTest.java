// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.api.datetime.DateTimeCalculation.startOfDay;
import static org.lfenergy.shapeshifter.connector.model.UftpMessageFixture.createOutgoing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.DPrognosis;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PeriodFutureOrTodayValidatorTest {

  private static final String TIME_ZONE_BUCHAREST = "Europe/Bucharest";
  private static final String TIME_ZONE_AMSTERDAM = "Europe/Amsterdam";

  @InjectMocks
  private PeriodFutureOrTodayValidator testSubject;

  @Mock
  private UftpParticipant sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        sender
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexMessageType.class)).isTrue();
    assertThat(testSubject.appliesTo(DPrognosis.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexReservationUpdate.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexRequest.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withoutParameter() {
    return Stream.of(
        Arguments.of(new DPrognosis()),
        Arguments.of(new FlexReservationUpdate()),
        Arguments.of(new FlexRequest()),
        Arguments.of(new FlexOffer()),
        Arguments.of(new FlexOrder())
    );
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenNoValueIsPresent(FlexMessageType flexMessage) {
    assertThat(testSubject.isValid(createOutgoing(sender, flexMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenPeriodInFuture(FlexMessageType flexMessage) {
    flexMessage.setPeriod(startOfDay(OffsetDateTime.now()).plusDays(1));
    flexMessage.setTimeZone(TIME_ZONE_AMSTERDAM);

    assertThat(testSubject.isValid(createOutgoing(sender, flexMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenToday(FlexMessageType flexMessage) {
    flexMessage.setPeriod(startOfDay(OffsetDateTime.now()));
    flexMessage.setTimeZone(TIME_ZONE_AMSTERDAM);

    assertThat(testSubject.isValid(createOutgoing(sender, flexMessage))).isTrue();
  }

  @Test
  void valid_true_today_from_bucharest() {
    TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE_AMSTERDAM));

    var flexRequest = new FlexRequest();
    flexRequest.setPeriod(startOfDay(ZonedDateTime.now(ZoneId.of(TIME_ZONE_BUCHAREST))).toOffsetDateTime());
    flexRequest.setTimeZone(TIME_ZONE_BUCHAREST);

    assertThat(testSubject.isValid(createOutgoing(sender, flexRequest))).isTrue();
  }

  @Test
  void valid_true_today_from_amsterdam() {
    TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE_AMSTERDAM));

    var flexRequest = new FlexRequest();
    flexRequest.setPeriod(startOfDay(ZonedDateTime.now(ZoneId.of(TIME_ZONE_AMSTERDAM)).toOffsetDateTime()));
    flexRequest.setTimeZone(TIME_ZONE_AMSTERDAM);

    assertThat(testSubject.isValid(createOutgoing(sender, flexRequest))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_false_whenBeforeToday(FlexMessageType flexMessage) {
    flexMessage.setPeriod(startOfDay(OffsetDateTime.now()).minusDays(1));
    flexMessage.setTimeZone(TIME_ZONE_AMSTERDAM);

    assertThat(testSubject.isValid(createOutgoing(sender, flexMessage))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Period out of bounds");
  }
}