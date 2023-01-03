package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.connector.service.validation.base.IspCollectorValidator.IspInfo;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IspConflictsValidatorTest {

  // Is not validated by this validation, but required for the base class of ISP validations.
  private static final long MAX_NUMER_ISPS = 1;

  @InjectMocks
  private IspConflictsValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("ISP conflict");
  }

  public static Stream<Arguments> for_validateIsps_failsWhenTwiceSameSimpleItem() {
    return Stream.of(
        Arguments.of(List.of(
            new IspInfo(1, 1),
            new IspInfo(1, 1)
        )),
        Arguments.of(List.of(
            new IspInfo(4, 1),
            new IspInfo(4, 1)
        )),
        Arguments.of(List.of(
            new IspInfo(20, 1),
            new IspInfo(20, 1)
        )),
        Arguments.of(List.of(
            new IspInfo(200, 1),
            new IspInfo(200, 1)
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("for_validateIsps_failsWhenTwiceSameSimpleItem")
  void validateIsps_failsWhenTwiceSameSimpleItem(List<IspInfo> ispList) {
    assertThat(testSubject.validateIsps(MAX_NUMER_ISPS, ispList)).isFalse();
  }

  public static Stream<Arguments> for_validateIsps_failsWhenThereIsOverlapDueToDuration() {
    return Stream.of(
        Arguments.of(List.of(
            new IspInfo(1, 10),
            new IspInfo(5, 1)
        )),
        Arguments.of(List.of(
            new IspInfo(1, 10),
            new IspInfo(10, 1)
        )),
        Arguments.of(List.of(
            new IspInfo(21, 10),
            new IspInfo(30, 10)
        )),
        Arguments.of(List.of(
            new IspInfo(1, 200),
            new IspInfo(150, 10)
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("for_validateIsps_failsWhenThereIsOverlapDueToDuration")
  void validateIsps_failsWhenThereIsOverlapDueToDuration(List<IspInfo> ispList) {
    assertThat(testSubject.validateIsps(MAX_NUMER_ISPS, ispList)).isFalse();
  }

  public static Stream<Arguments> for_validateIsps_successWhenThereIsNoOverlap() {
    return Stream.of(
        Arguments.of(List.of(
            new IspInfo(1, 10),
            new IspInfo(11, 10),
            new IspInfo(21, 10),
            new IspInfo(31, 10),
            new IspInfo(41, 10),
            new IspInfo(51, 10)
        )),
        Arguments.of(List.of(
            new IspInfo(1, 10)
        )),
        Arguments.of(List.of(
            new IspInfo(20, 10),
            new IspInfo(30, 10)
        )),
        Arguments.of(List.of(
            new IspInfo(1, 1),
            new IspInfo(20, 10),
            new IspInfo(150, 10)
        ))
    );
  }

  @ParameterizedTest
  @MethodSource("for_validateIsps_successWhenThereIsNoOverlap")
  void validateIsps_successWhenThereIsNoOverlap(List<IspInfo> ispList) {
    assertThat(testSubject.validateIsps(MAX_NUMER_ISPS, ispList)).isTrue();
  }
}