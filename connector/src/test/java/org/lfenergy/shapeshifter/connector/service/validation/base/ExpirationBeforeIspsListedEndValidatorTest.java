package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpirationBeforeIspsListedEndValidatorTest {

  private static final OffsetDateTime PERIOD = OffsetDateTime.parse("2022-11-22T00:00:00+01:00");
  private static final Duration DURATION_15_MINUTES = Duration.ofMinutes(15);
  private static final String TIME_ZONE_AMSTERDAM = "Europe/Amsterdam";

  @InjectMocks
  private ExpirationBeforeIspsListedEndValidator testSubject;

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

  public static Stream<Arguments> valid_whenExpirationBeforeLastIspEnd() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setExpirationDateTime(PERIOD.plusHours(6));
    // 15 min ISP max number at least 24. Is 50.
    flexRequest.getISPS().addAll(List.of(
        setStartAndDuration(new FlexRequestISPType(), 1, 1),
        setStartAndDuration(new FlexRequestISPType(), 2, 2),
        setStartAndDuration(new FlexRequestISPType(), 8, 3),
        setStartAndDuration(new FlexRequestISPType(), 21, 4), // end 24
        setStartAndDuration(new FlexRequestISPType(), 50, 1)  // end 50
    ));

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setExpirationDateTime(PERIOD.plusHours(6));
    // 15 min ISP max number at least 24. Is 4.
    FlexOfferOptionType option1 = new FlexOfferOptionType();
    option1.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOfferOptionISPType(), 1, 1),
        setStartAndDuration(new FlexOfferOptionISPType(), 2, 2)
    ));
    // 15 min ISP max number at least 24. Is 4.
    FlexOfferOptionType option2 = new FlexOfferOptionType();
    option2.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOfferOptionISPType(), 1, 1),
        setStartAndDuration(new FlexOfferOptionISPType(), 2, 2),
        setStartAndDuration(new FlexOfferOptionISPType(), 8, 3),
        setStartAndDuration(new FlexOfferOptionISPType(), 21, 4), // end 24
        setStartAndDuration(new FlexOfferOptionISPType(), 50, 1)  // end 50
    ));
    // Option2 allows the set expiration time
    flexOffer.getOfferOptions().addAll(List.of(option1, option2));

    return Stream.of(
        Arguments.of(flexRequest),
        Arguments.of(flexOffer)
    );
  }

  @ParameterizedTest
  @MethodSource("valid_whenExpirationBeforeLastIspEnd")
  void valid_whenExpirationBeforeLastIspEnd(FlexMessageType flexMessage) {
    flexMessage.setPeriod(PERIOD);
    flexMessage.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexMessage.setISPDuration(DURATION_15_MINUTES);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, flexMessage))).isTrue();
  }

  @Test
  void test_period_23h_utc() {
    var isp = new FlexRequestISPType();
    isp.setStart(1L);
    isp.setMaxPower(50000000L);
    isp.setDuration(1L);

    var flexRequest = new FlexRequest();
    flexRequest.setPeriod(OffsetDateTime.of(2023, 1, 17, 23, 0, 0, 0, ZoneOffset.UTC));
    flexRequest.setExpirationDateTime(OffsetDateTime.of(2023, 1, 17, 11, 0, 0, 0, ZoneOffset.UTC));
    flexRequest.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexRequest.setISPDuration(DURATION_15_MINUTES);
    flexRequest.getISPS().add(isp);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, flexRequest))).isTrue();
  }

  @Test
  void test_expiration_date_equal_to_end_time_of_last_isp_of_day() {
    var isp = new FlexRequestISPType();
    isp.setStart(96L);
    isp.setMaxPower(50000000L);
    isp.setDuration(1L);

    var flexRequest = new FlexRequest();
    flexRequest.setPeriod(OffsetDateTime.of(2023, 1, 17, 23, 0, 0, 0, ZoneOffset.UTC));
    flexRequest.setExpirationDateTime(OffsetDateTime.of(2023, 1, 18, 22, 45, 0, 0, ZoneOffset.UTC));
    flexRequest.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexRequest.setISPDuration(DURATION_15_MINUTES);
    flexRequest.getISPS().add(isp);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, flexRequest))).isTrue();
  }

  public static Stream<Arguments> valid_whenExpirationEqualToLastIspEnd() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setExpirationDateTime(PERIOD.plusHours(6));
    // 15 min ISP max number at least 24. Is 24.
    flexRequest.getISPS().addAll(List.of(
        setStartAndDuration(new FlexRequestISPType(), 1, 1),
        setStartAndDuration(new FlexRequestISPType(), 2, 2),
        setStartAndDuration(new FlexRequestISPType(), 8, 3),
        setStartAndDuration(new FlexRequestISPType(), 21, 4) // end 24
    ));

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setExpirationDateTime(PERIOD.plusHours(6));
    // 15 min ISP max number at least 24. Is 4.
    FlexOfferOptionType option1 = new FlexOfferOptionType();
    option1.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOfferOptionISPType(), 1, 1),
        setStartAndDuration(new FlexOfferOptionISPType(), 2, 2)
    ));
    // 15 min ISP max number at least 24. Is 24.
    FlexOfferOptionType option2 = new FlexOfferOptionType();
    option2.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOfferOptionISPType(), 1, 1),
        setStartAndDuration(new FlexOfferOptionISPType(), 2, 2),
        setStartAndDuration(new FlexOfferOptionISPType(), 8, 3),
        setStartAndDuration(new FlexOfferOptionISPType(), 21, 4) // end 24
    ));
    // Option2 allows the set expiration time
    flexOffer.getOfferOptions().addAll(List.of(option1, option2));

    return Stream.of(
        Arguments.of(flexRequest),
        Arguments.of(flexOffer)
    );
  }

  @ParameterizedTest
  @MethodSource("valid_whenExpirationEqualToLastIspEnd")
  void valid_whenExpirationEqualToLastIspEnd(FlexMessageType flexMessage) {
    flexMessage.setPeriod(PERIOD);
    flexMessage.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexMessage.setISPDuration(DURATION_15_MINUTES);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, flexMessage))).isTrue();
  }

  public static Stream<Arguments> valid_false_whenExpirationAfterLastIspEnd() {
    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setExpirationDateTime(PERIOD.plusHours(6));
    // 15 min ISP max number at least 24. Is 23.
    flexRequest.getISPS().addAll(List.of(
        setStartAndDuration(new FlexRequestISPType(), 21, 3)
    ));

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setExpirationDateTime(PERIOD.plusHours(6));
    // 15 min ISP max number at least 24. Is 4.
    FlexOfferOptionType option1 = new FlexOfferOptionType();
    option1.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOfferOptionISPType(), 2, 2)
    ));
    // 15 min ISP max number at least 24. Is 23.
    FlexOfferOptionType option2 = new FlexOfferOptionType();
    option2.getISPS().addAll(List.of(
        setStartAndDuration(new FlexOfferOptionISPType(), 21, 3) // end 23
    ));
    // Option2 allows the set expiration time
    flexOffer.getOfferOptions().addAll(List.of(option1, option2));

    return Stream.of(
        Arguments.of(flexRequest),
        Arguments.of(flexOffer)
    );
  }

  @ParameterizedTest
  @MethodSource("valid_false_whenExpirationAfterLastIspEnd")
  void valid_false_whenExpirationAfterLastIspEnd(FlexMessageType flexMessage) {
    flexMessage.setPeriod(PERIOD);
    flexMessage.setTimeZone(TIME_ZONE_AMSTERDAM);
    flexMessage.setISPDuration(DURATION_15_MINUTES);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, flexMessage))).isFalse();
  }

  @Test
  void valid_throws_whenNoIspsInner() {
    FlexRequest flexRequest = new FlexRequest();

    var uftpMessage = UftpMessageFixture.<PayloadMessageType>createOutgoing(sender, flexRequest);
    IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
        testSubject.valid(uftpMessage));

    assertThat(thrown.getMessage()).isEqualTo("No ISPs found");
  }

  @Test
  void valid_throws_whenNoIspsOuter() {
    FlexOffer flexOffer = new FlexOffer();

    var uftpMessage = UftpMessageFixture.<PayloadMessageType>createOutgoing(sender, flexOffer);
    IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
        testSubject.valid(uftpMessage));

    assertThat(thrown.getMessage()).isEqualTo("No ISPs found");
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

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("ExpirationDateTime out of bounds (ISP's related)");
  }
}
