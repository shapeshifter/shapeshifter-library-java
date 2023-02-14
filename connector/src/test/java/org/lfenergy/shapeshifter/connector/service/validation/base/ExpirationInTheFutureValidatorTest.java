package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.OffsetDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpirationInTheFutureValidatorTest {

  @InjectMocks
  private ExpirationInTheFutureValidator testSubject;

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
    assertThat(testSubject.appliesTo(FlexRequest.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> valid_whenNotPresent() {
    return Stream.of(
        Arguments.of(new FlexRequest()),
        Arguments.of(new FlexOffer())
    );
  }

  @ParameterizedTest
  @MethodSource("valid_whenNotPresent")
  void valid_whenNotPresent(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  public static Stream<Arguments> valid_whenInTheFuture() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setExpirationDateTime(OffsetDateTime.now().plusDays(1));

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setExpirationDateTime(OffsetDateTime.now().plusDays(1));

    return Stream.of(
        Arguments.of(flexRequest),
        Arguments.of(flexOffer)
    );
  }

  @ParameterizedTest
  @MethodSource("valid_whenInTheFuture")
  void valid_whenInTheFuture(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  public static Stream<Arguments> valid_false_whenNow() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setExpirationDateTime(OffsetDateTime.now());

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setExpirationDateTime(OffsetDateTime.now());

    return Stream.of(
        Arguments.of(flexRequest),
        Arguments.of(flexOffer)
    );
  }

  @ParameterizedTest
  @MethodSource("valid_false_whenNow")
  void valid_false_whenNow(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  public static Stream<Arguments> valid_whenInThePassed() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setExpirationDateTime(OffsetDateTime.now().minusDays(1));

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setExpirationDateTime(OffsetDateTime.now().minusDays(1));

    return Stream.of(
        Arguments.of(flexRequest),
        Arguments.of(flexOffer)
    );
  }

  @ParameterizedTest
  @MethodSource("valid_whenInThePassed")
  void valid_whenInThePassed(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("ExpirationDateTime out of bounds");
  }
}