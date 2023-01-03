package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.allMessageTypes;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.flexMessageTypes;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IspDurationValidatorTest {

  private static final Duration DURATION = Duration.ofMinutes(15);

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private IspDurationValidator testSubject;

  @Mock
  private UftpParticipant sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        sender
    );
  }

  public static Stream<Class<? extends PayloadMessageType>> appliesToStream() {
    return Stream.concat(
        flexMessageTypes().stream(),
        Stream.of(Metering.class, FlexMessageType.class)
    );
  }

  public static Stream<Arguments> appliesToTypes() {
    return appliesToStream().map(Arguments::of);
  }

  @ParameterizedTest
  @MethodSource("appliesToTypes")
  void appliesTo(Class<? extends PayloadMessageType> type) {
    assertThat(testSubject.appliesTo(type)).isTrue();
  }

  public static Stream<Arguments> notAppliesToTypes() {
    var allowed = appliesToStream().collect(Collectors.toSet());
    return allMessageTypes().stream()
                            .filter(t -> !allowed.contains(t))
                            .map(Arguments::of);
  }

  @ParameterizedTest
  @MethodSource("notAppliesToTypes")
  void notAppliesTo(Class<? extends PayloadMessageType> type) {
    assertThat(testSubject.appliesTo(type)).isFalse();
  }

  public static Stream<Arguments> withoutParameter() {
    return Stream.of(
        Arguments.of(new FlexRequest()),
        Arguments.of(new Metering())
    );
  }

  public static Stream<Arguments> withParameter() {

    FlexRequest flexRequest = new FlexRequest();
    flexRequest.setISPDuration(DURATION);
    Metering meteringMessage = new Metering();
    meteringMessage.setISPDuration(DURATION);

    return Stream.of(
        Arguments.of(flexRequest),
        Arguments.of(meteringMessage)
    );
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenNoDurationIsPresent(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(sender, payloadMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_true_whenFoundDurationIsSupported(PayloadMessageType payloadMessage) {
    given(support.isSupportedIspDuration(DURATION)).willReturn(true);

    assertThat(testSubject.valid(sender, payloadMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_false_whenFoundDurationIsNotSupported(PayloadMessageType payloadMessage) {
    given(support.isSupportedIspDuration(DURATION)).willReturn(false);

    assertThat(testSubject.valid(sender, payloadMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("ISP duration rejected");
  }
}
