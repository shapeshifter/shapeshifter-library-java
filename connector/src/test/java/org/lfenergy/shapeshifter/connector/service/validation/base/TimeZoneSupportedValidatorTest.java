package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.AGRPortfolioQuery;
import org.lfenergy.shapeshifter.api.AGRPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.AGRPortfolioUpdate;
import org.lfenergy.shapeshifter.api.DPrognosis;
import org.lfenergy.shapeshifter.api.DSOPortfolioQuery;
import org.lfenergy.shapeshifter.api.DSOPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdate;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimeZoneSupportedValidatorTest {

  private static final String TIME_ZONE = "TIME_ZONE";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private TimeZoneSupportedValidator testSubject;

  @Mock
  private UftpParticipant sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        sender
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(AGRPortfolioUpdate.class)).isTrue();
    assertThat(testSubject.appliesTo(AGRPortfolioQuery.class)).isTrue();
    assertThat(testSubject.appliesTo(AGRPortfolioQueryResponse.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexMessageType.class)).isTrue();
    assertThat(testSubject.appliesTo(DPrognosis.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexReservationUpdate.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexRequest.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
    assertThat(testSubject.appliesTo(DSOPortfolioUpdate.class)).isTrue();
    assertThat(testSubject.appliesTo(DSOPortfolioQuery.class)).isTrue();
    assertThat(testSubject.appliesTo(DSOPortfolioQueryResponse.class)).isTrue();
    assertThat(testSubject.appliesTo(Metering.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withoutParameter() {
    return Stream.of(
        Arguments.of(new AGRPortfolioUpdate()),
        Arguments.of(new AGRPortfolioQuery()),
        Arguments.of(new AGRPortfolioQueryResponse()),
        Arguments.of(new FlexRequest()),
        Arguments.of(new DSOPortfolioUpdate()),
        Arguments.of(new DSOPortfolioQuery()),
        Arguments.of(new DSOPortfolioQueryResponse()),
        Arguments.of(new Metering())
    );
  }

  public static Stream<Arguments> withParameter() {

    AGRPortfolioUpdate agrPortfolioUpdate = new AGRPortfolioUpdate();
    agrPortfolioUpdate.setTimeZone(TIME_ZONE);
    AGRPortfolioQuery agrPortfolioQuery = new AGRPortfolioQuery();
    agrPortfolioQuery.setTimeZone(TIME_ZONE);
    AGRPortfolioQueryResponse agrPortfolioQueryResponse = new AGRPortfolioQueryResponse();
    agrPortfolioQueryResponse.setTimeZone(TIME_ZONE);
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setTimeZone(TIME_ZONE);
    DSOPortfolioUpdate dSOPortfolioUpdate = new DSOPortfolioUpdate();
    dSOPortfolioUpdate.setTimeZone(TIME_ZONE);
    DSOPortfolioQuery dSOPortfolioQuery = new DSOPortfolioQuery();
    dSOPortfolioQuery.setTimeZone(TIME_ZONE);
    DSOPortfolioQueryResponse dSOPortfolioQueryResponse = new DSOPortfolioQueryResponse();
    dSOPortfolioQueryResponse.setTimeZone(TIME_ZONE);
    Metering meteringMessage = new Metering();
    meteringMessage.setTimeZone(TIME_ZONE);

    return Stream.of(
        Arguments.of(agrPortfolioUpdate),
        Arguments.of(agrPortfolioQuery),
        Arguments.of(agrPortfolioQueryResponse),
        Arguments.of(flexRequest),
        Arguments.of(dSOPortfolioUpdate),
        Arguments.of(dSOPortfolioQuery),
        Arguments.of(dSOPortfolioQueryResponse),
        Arguments.of(meteringMessage)
    );
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenNoValueIsPresent(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(sender, payloadMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_true_whenFoundValueIsSupported(PayloadMessageType payloadMessage) {
    given(support.isSupportedTimeZone(TIME_ZONE)).willReturn(true);

    assertThat(testSubject.valid(sender, payloadMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_false_whenFoundValueIsNotSupported(PayloadMessageType payloadMessage) {
    given(support.isSupportedTimeZone(TIME_ZONE)).willReturn(false);

    assertThat(testSubject.valid(sender, payloadMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Time zone rejected");
  }
}