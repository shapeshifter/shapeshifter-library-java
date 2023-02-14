package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Set;
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
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CongestionPointValidatorTest {

  private static final String CONGESTION_POINT1 = "CONGESTION_POINT1";
  private static final String CONGESTION_POINT2 = "CONGESTION_POINT2";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private CongestionPointValidator testSubject;

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
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(FlexMessageType.class)).isTrue();
    assertThat(testSubject.appliesTo(DPrognosis.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexReservationUpdate.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexRequest.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexSettlement.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withoutParameter() {
    return Stream.of(
        Arguments.of(new FlexRequest()),
        Arguments.of(new FlexSettlement())
    );
  }

  public static Stream<Arguments> withParameter() {

    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setCongestionPoint(CONGESTION_POINT1);

    FlexOrderSettlementType t1 = new FlexOrderSettlementType();
    t1.setCongestionPoint(CONGESTION_POINT1);
    FlexOrderSettlementType t2 = new FlexOrderSettlementType();
    t2.setCongestionPoint(CONGESTION_POINT2);

    FlexSettlement flexSettlement = new FlexSettlement();
    flexSettlement.getFlexOrderSettlements().addAll(List.of(t1, t2, new FlexOrderSettlementType()));

    return Stream.of(
        Arguments.of(flexRequest, Set.of(CONGESTION_POINT1)),
        Arguments.of(flexSettlement, Set.of(CONGESTION_POINT1, CONGESTION_POINT2))
    );
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenNoValueIsPresent(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_true_whenFoundValueIsSupported(PayloadMessageType payloadMessage, Set<String> congestionPoints) {
    given(support.areKnownCongestionPoints(congestionPoints)).willReturn(true);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_false_whenFoundValueIsNotSupported(PayloadMessageType payloadMessage, Set<String> congestionPoints) {
    given(support.areKnownCongestionPoints(congestionPoints)).willReturn(false);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Invalid congestion point");
  }
}