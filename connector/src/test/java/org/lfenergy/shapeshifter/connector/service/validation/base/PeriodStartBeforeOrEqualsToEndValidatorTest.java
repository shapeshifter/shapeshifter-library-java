package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.AGRPortfolioUpdate;
import org.lfenergy.shapeshifter.api.AGRPortfolioUpdateConnection;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdate;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateCongestionPoint;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateConnectionType;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PeriodStartBeforeOrEqualsToEndValidatorTest {

  private static final OffsetDateTime WINTER_TIME_DAY = OffsetDateTime.parse("2022-11-22T00:00:00+01:00");
  private static final OffsetDateTime INFINITE = null;

  @InjectMocks
  private PeriodStartBeforeOrEqualsToEndValidator testSubject;

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
    assertThat(testSubject.appliesTo(FlexSettlement.class)).isTrue();
    assertThat(testSubject.appliesTo(AGRPortfolioUpdate.class)).isTrue();
    assertThat(testSubject.appliesTo(DSOPortfolioUpdate.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> validStartEndPeriodsOnly() {
    FlexSettlement flexSettlementEquals = new FlexSettlement();
    flexSettlementEquals.setPeriodStart(WINTER_TIME_DAY);
    flexSettlementEquals.setPeriodEnd(WINTER_TIME_DAY);

    FlexSettlement flexSettlementEndLater = new FlexSettlement();
    flexSettlementEndLater.setPeriodStart(WINTER_TIME_DAY);
    flexSettlementEndLater.setPeriodEnd(WINTER_TIME_DAY.plusDays(1));

    FlexSettlement flexSettlementEndInfinite = new FlexSettlement();
    flexSettlementEndInfinite.setPeriodStart(WINTER_TIME_DAY);
    flexSettlementEndInfinite.setPeriodEnd(INFINITE);

    AGRPortfolioUpdateConnection agrConnEquals = new AGRPortfolioUpdateConnection();
    agrConnEquals.setStartPeriod(WINTER_TIME_DAY);
    agrConnEquals.setEndPeriod(WINTER_TIME_DAY);

    AGRPortfolioUpdateConnection agrConnEndLater = new AGRPortfolioUpdateConnection();
    agrConnEndLater.setStartPeriod(WINTER_TIME_DAY);
    agrConnEndLater.setEndPeriod(WINTER_TIME_DAY.plusDays(4));

    AGRPortfolioUpdateConnection agrConnEndInfinite = new AGRPortfolioUpdateConnection();
    agrConnEndInfinite.setStartPeriod(WINTER_TIME_DAY);
    agrConnEndInfinite.setEndPeriod(INFINITE);

    AGRPortfolioUpdate agrPortfolioUpdate = new AGRPortfolioUpdate();
    agrPortfolioUpdate.getConnections().addAll(List.of(agrConnEquals, agrConnEndLater, agrConnEndInfinite));

    DSOPortfolioUpdateConnectionType dsoConnEquals = new DSOPortfolioUpdateConnectionType();
    dsoConnEquals.setStartPeriod(WINTER_TIME_DAY);
    dsoConnEquals.setEndPeriod(WINTER_TIME_DAY);

    DSOPortfolioUpdateConnectionType dsoConnEndLater = new DSOPortfolioUpdateConnectionType();
    dsoConnEndLater.setStartPeriod(WINTER_TIME_DAY);
    dsoConnEndLater.setEndPeriod(WINTER_TIME_DAY.plusDays(4));

    DSOPortfolioUpdateConnectionType dsoConnEndInfinite = new DSOPortfolioUpdateConnectionType();
    dsoConnEndInfinite.setStartPeriod(WINTER_TIME_DAY);
    dsoConnEndInfinite.setEndPeriod(INFINITE);

    DSOPortfolioUpdateCongestionPoint dsoPointEquals = new DSOPortfolioUpdateCongestionPoint();
    dsoPointEquals.setStartPeriod(WINTER_TIME_DAY);
    dsoPointEquals.setEndPeriod(WINTER_TIME_DAY);
    dsoPointEquals.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite));

    DSOPortfolioUpdateCongestionPoint dsoPointEndLater = new DSOPortfolioUpdateCongestionPoint();
    dsoPointEndLater.setStartPeriod(WINTER_TIME_DAY);
    dsoPointEndLater.setEndPeriod(WINTER_TIME_DAY.plusDays(4));
    dsoPointEndLater.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite));

    DSOPortfolioUpdateCongestionPoint dsoPointEndInfinite = new DSOPortfolioUpdateCongestionPoint();
    dsoPointEndInfinite.setStartPeriod(WINTER_TIME_DAY);
    dsoPointEndInfinite.setEndPeriod(INFINITE);
    dsoPointEndInfinite.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite));

    DSOPortfolioUpdate dsoPortfolioUpdate = new DSOPortfolioUpdate();
    dsoPortfolioUpdate.getCongestionPoints().addAll(List.of(dsoPointEquals, dsoPointEndLater, dsoPointEndInfinite));

    return Stream.of(
        Arguments.of(flexSettlementEquals),
        Arguments.of(flexSettlementEndLater),
        Arguments.of(flexSettlementEndInfinite),
        Arguments.of(agrPortfolioUpdate),
        Arguments.of(dsoPortfolioUpdate)
    );
  }

  @ParameterizedTest
  @MethodSource("validStartEndPeriodsOnly")
  void valid_true_whenStartAlwaysEqualsToOrBeforeEnd(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  public static Stream<Arguments> invalidStartEndPeriodsAsWell() {
    FlexSettlement fsInvalidEndBeforeStart = new FlexSettlement();
    fsInvalidEndBeforeStart.setPeriodStart(WINTER_TIME_DAY);
    fsInvalidEndBeforeStart.setPeriodEnd(WINTER_TIME_DAY.minusDays(1));

    FlexSettlement fsInvalidStartInfinite = new FlexSettlement();
    fsInvalidStartInfinite.setPeriodStart(INFINITE);
    fsInvalidStartInfinite.setPeriodEnd(WINTER_TIME_DAY);

    FlexSettlement fsInvalidBothInfinite = new FlexSettlement();
    fsInvalidBothInfinite.setPeriodStart(INFINITE);
    fsInvalidBothInfinite.setPeriodEnd(INFINITE);

    AGRPortfolioUpdateConnection agrConnEquals = new AGRPortfolioUpdateConnection();
    agrConnEquals.setStartPeriod(WINTER_TIME_DAY);
    agrConnEquals.setEndPeriod(WINTER_TIME_DAY);

    AGRPortfolioUpdateConnection agrConnEndLater = new AGRPortfolioUpdateConnection();
    agrConnEndLater.setStartPeriod(WINTER_TIME_DAY);
    agrConnEndLater.setEndPeriod(WINTER_TIME_DAY.plusDays(4));

    AGRPortfolioUpdateConnection agrConnEndInfinite = new AGRPortfolioUpdateConnection();
    agrConnEndInfinite.setStartPeriod(WINTER_TIME_DAY);
    agrConnEndInfinite.setEndPeriod(INFINITE);

    AGRPortfolioUpdateConnection acInvalidEndBeforeStart = new AGRPortfolioUpdateConnection();
    acInvalidEndBeforeStart.setStartPeriod(WINTER_TIME_DAY);
    acInvalidEndBeforeStart.setEndPeriod(WINTER_TIME_DAY.minusDays(5));

    AGRPortfolioUpdateConnection acInvalidStartInfinite = new AGRPortfolioUpdateConnection();
    acInvalidStartInfinite.setStartPeriod(INFINITE);
    acInvalidStartInfinite.setEndPeriod(WINTER_TIME_DAY);

    AGRPortfolioUpdate agrPortfolioUpdate1 = new AGRPortfolioUpdate();
    agrPortfolioUpdate1.getConnections().addAll(List.of(agrConnEquals, agrConnEndLater, agrConnEndInfinite, acInvalidEndBeforeStart));

    AGRPortfolioUpdate agrPortfolioUpdate2 = new AGRPortfolioUpdate();
    agrPortfolioUpdate2.getConnections().addAll(List.of(agrConnEquals, agrConnEndLater, agrConnEndInfinite, acInvalidStartInfinite));

    DSOPortfolioUpdateConnectionType dsoConnEquals = new DSOPortfolioUpdateConnectionType();
    dsoConnEquals.setStartPeriod(WINTER_TIME_DAY);
    dsoConnEquals.setEndPeriod(WINTER_TIME_DAY);

    DSOPortfolioUpdateConnectionType dsoConnEndLater = new DSOPortfolioUpdateConnectionType();
    dsoConnEndLater.setStartPeriod(WINTER_TIME_DAY);
    dsoConnEndLater.setEndPeriod(WINTER_TIME_DAY.plusDays(4));

    DSOPortfolioUpdateConnectionType dsoConnEndInfinite = new DSOPortfolioUpdateConnectionType();
    dsoConnEndInfinite.setStartPeriod(WINTER_TIME_DAY);
    dsoConnEndInfinite.setEndPeriod(INFINITE);

    DSOPortfolioUpdateConnectionType dcInvalidEndBeforeStart = new DSOPortfolioUpdateConnectionType();
    dcInvalidEndBeforeStart.setStartPeriod(WINTER_TIME_DAY);
    dcInvalidEndBeforeStart.setEndPeriod(WINTER_TIME_DAY.minusDays(3));

    DSOPortfolioUpdateConnectionType dcInvalidStartInfinite = new DSOPortfolioUpdateConnectionType();
    dcInvalidStartInfinite.setStartPeriod(INFINITE);
    dcInvalidStartInfinite.setEndPeriod(WINTER_TIME_DAY);

    DSOPortfolioUpdateCongestionPoint dsoPointEquals = new DSOPortfolioUpdateCongestionPoint();
    dsoPointEquals.setStartPeriod(WINTER_TIME_DAY);
    dsoPointEquals.setEndPeriod(WINTER_TIME_DAY);
    dsoPointEquals.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite));

    DSOPortfolioUpdateCongestionPoint dsoPointEndLater = new DSOPortfolioUpdateCongestionPoint();
    dsoPointEndLater.setStartPeriod(WINTER_TIME_DAY);
    dsoPointEndLater.setEndPeriod(WINTER_TIME_DAY.plusDays(4));
    dsoPointEndLater.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite));

    DSOPortfolioUpdateCongestionPoint dsoPointEndInfinite = new DSOPortfolioUpdateCongestionPoint();
    dsoPointEndInfinite.setStartPeriod(WINTER_TIME_DAY);
    dsoPointEndInfinite.setEndPeriod(INFINITE);
    dsoPointEndInfinite.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite));

    DSOPortfolioUpdateCongestionPoint dpInvalidEndBeforeStart = new DSOPortfolioUpdateCongestionPoint();
    dpInvalidEndBeforeStart.setStartPeriod(WINTER_TIME_DAY);
    dpInvalidEndBeforeStart.setEndPeriod(WINTER_TIME_DAY.minusDays(8));
    dpInvalidEndBeforeStart.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite));

    DSOPortfolioUpdateCongestionPoint dpInvalidStartInfinite = new DSOPortfolioUpdateCongestionPoint();
    dpInvalidStartInfinite.setStartPeriod(INFINITE);
    dpInvalidStartInfinite.setEndPeriod(WINTER_TIME_DAY);
    dpInvalidStartInfinite.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite));

    DSOPortfolioUpdateCongestionPoint dpInvalidInList1 = new DSOPortfolioUpdateCongestionPoint();
    dpInvalidInList1.setStartPeriod(WINTER_TIME_DAY);
    dpInvalidInList1.setEndPeriod(INFINITE);
    dpInvalidInList1.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite, dcInvalidEndBeforeStart));

    DSOPortfolioUpdateCongestionPoint dpInvalidInList2 = new DSOPortfolioUpdateCongestionPoint();
    dpInvalidInList2.setStartPeriod(WINTER_TIME_DAY);
    dpInvalidInList2.setEndPeriod(INFINITE);
    dpInvalidInList2.getConnections().addAll(List.of(dsoConnEquals, dsoConnEndLater, dsoConnEndInfinite, dcInvalidStartInfinite));

    DSOPortfolioUpdate dsoPortfolioUpdate1 = new DSOPortfolioUpdate();
    dsoPortfolioUpdate1.getCongestionPoints().addAll(List.of(dsoPointEquals, dsoPointEndLater, dsoPointEndInfinite, dpInvalidStartInfinite));

    DSOPortfolioUpdate dsoPortfolioUpdate2 = new DSOPortfolioUpdate();
    dsoPortfolioUpdate2.getCongestionPoints().addAll(List.of(dsoPointEquals, dsoPointEndLater, dsoPointEndInfinite, dpInvalidEndBeforeStart));

    DSOPortfolioUpdate dsoPortfolioUpdate3 = new DSOPortfolioUpdate();
    dsoPortfolioUpdate3.getCongestionPoints().addAll(List.of(dsoPointEquals, dsoPointEndLater, dsoPointEndInfinite, dpInvalidInList1));

    DSOPortfolioUpdate dsoPortfolioUpdate4 = new DSOPortfolioUpdate();
    dsoPortfolioUpdate4.getCongestionPoints().addAll(List.of(dsoPointEquals, dsoPointEndLater, dsoPointEndInfinite, dpInvalidInList2));

    return Stream.of(
        Arguments.of(fsInvalidEndBeforeStart),
        Arguments.of(fsInvalidStartInfinite),
        Arguments.of(fsInvalidBothInfinite),
        Arguments.of(agrPortfolioUpdate1),
        Arguments.of(agrPortfolioUpdate2),
        Arguments.of(dsoPortfolioUpdate1),
        Arguments.of(dsoPortfolioUpdate2),
        Arguments.of(dsoPortfolioUpdate3),
        Arguments.of(dsoPortfolioUpdate4)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidStartEndPeriodsAsWell")
  void valid_false_whenThereIsAStartLaterThenEnd(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Period out of bounds");
  }
}