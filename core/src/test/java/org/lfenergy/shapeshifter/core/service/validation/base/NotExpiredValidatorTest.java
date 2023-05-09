// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.TIME_ZONE_AMSTERDAM;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.TIME_ZONE_MEXICO_CITY;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotExpiredValidatorTest {

  private static final String MATCHING_MESSAGE_ID = "MATCHING_MESSAGE_ID";
  private static final LocalDate PERIOD = LocalDate.of(2022, 11, 22);

  @Mock
  private UftpMessageSupport messageSupport;

  @InjectMocks
  private NotExpiredValidator testSubject;

  @Mock
  private UftpParticipant sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        messageSupport,
        sender
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withParameter() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setTimeZone(TIME_ZONE_AMSTERDAM);

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexOffer.setFlexRequestMessageID(MATCHING_MESSAGE_ID);
    flexOffer.setPeriod(PERIOD);

    FlexOrder flexOrder = new FlexOrder();
    flexOrder.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexOrder.setFlexOfferMessageID(MATCHING_MESSAGE_ID);
    flexOrder.setPeriod(PERIOD);

    return Stream.of(
        Arguments.of(flexOffer, FlexRequest.class, flexRequest),
        Arguments.of(flexOrder, FlexOffer.class, flexOffer)
    );
  }

  @Test
  void valid_true_whenFlexOfferTypeWhenThereIsNoFlexRequestMessageID() {
    // Matching Message ID is mandatory except for FlexOffer's FlexRequestMessageID
    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setFlexRequestMessageID(null);
    flexOffer.setPeriod(PERIOD);

    assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isTrue();
  }

  @Test
  void valid_flexOffer_true_whenFlexRequestIsSentFromMexicoCity() {
    TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE_AMSTERDAM));

    var flexRequest = new FlexRequest();
    flexRequest.setExpirationDateTime(ZonedDateTime.now(ZoneId.of(TIME_ZONE_MEXICO_CITY)).plusMinutes(1).toOffsetDateTime());
    flexRequest.setTimeZone(TIME_ZONE_MEXICO_CITY);

    var flexOffer = new FlexOffer();
    flexOffer.setFlexRequestMessageID(MATCHING_MESSAGE_ID);

    var uftpMessage = UftpMessageFixture.<PayloadMessageType>createOutgoing(sender, flexOffer);

    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(MATCHING_MESSAGE_ID, FlexRequest.class))).willReturn(Optional.of(flexRequest));

    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @Test
  void valid_flexOrder_true_whenFlexOfferIsSentFromMexicoCity() {
    TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE_AMSTERDAM));

    var flexOffer = new FlexOffer();
    flexOffer.setExpirationDateTime(ZonedDateTime.now(ZoneId.of(TIME_ZONE_MEXICO_CITY)).plusMinutes(1).toOffsetDateTime());
    flexOffer.setTimeZone(TIME_ZONE_MEXICO_CITY);

    var flexOrder = new FlexOrder();
    flexOrder.setFlexOfferMessageID(MATCHING_MESSAGE_ID);

    var uftpMessage = UftpMessageFixture.<PayloadMessageType>createOutgoing(sender, flexOrder);

    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(MATCHING_MESSAGE_ID, FlexOffer.class))).willReturn(Optional.of(flexOffer));

    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  <T extends PayloadMessageType> void valid_true_matchingMessageNotFound(
      PayloadMessageType payloadMessage, Class<T> matchingMessageType, T matchingMessage
  ) {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);
    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(MATCHING_MESSAGE_ID, matchingMessageType))).willReturn(Optional.empty());

    // Matching message may not be found, although it should be there. This returns valid() == true
    // because that is not the purpose of this validation. It is checked in Referenced******MessageIdValidation classes.
    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  <T extends PayloadMessageType> void valid_true_matchingMessageFoundNotYetExpired(
      PayloadMessageType payloadMessage, Class<T> matchingMessageType, T matchingMessage
  ) throws Exception {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);
    setExpirationDateTime(matchingMessageType, matchingMessage, OffsetDateTime.now().plus(1, ChronoUnit.HOURS));
    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(MATCHING_MESSAGE_ID, matchingMessageType))).willReturn(Optional.of(matchingMessage));

    assertThat(testSubject.isValid(uftpMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  <T extends PayloadMessageType> void valid_false_matchingMessageFoundWhenExpired(
      PayloadMessageType payloadMessage, Class<T> matchingMessageType, T matchingMessage
  ) throws Exception {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);
    setExpirationDateTime(matchingMessageType, matchingMessage, OffsetDateTime.now().minus(1, ChronoUnit.HOURS));
    given(messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(MATCHING_MESSAGE_ID, matchingMessageType))).willReturn(Optional.of(matchingMessage));

    assertThat(testSubject.isValid(uftpMessage)).isFalse();
  }

  private <T extends PayloadMessageType> void setExpirationDateTime(Class<T> matchingMessageType, T matchingMessage, OffsetDateTime value) throws Exception {
    var setter = matchingMessageType.getDeclaredMethod("setExpirationDateTime", OffsetDateTime.class);
    setter.invoke(matchingMessage, value);
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Reference message expired");
  }
}