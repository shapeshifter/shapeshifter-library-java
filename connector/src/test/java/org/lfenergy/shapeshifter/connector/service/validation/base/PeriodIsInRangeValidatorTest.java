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
import org.lfenergy.shapeshifter.api.ContractSettlementPeriodType;
import org.lfenergy.shapeshifter.api.ContractSettlementType;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdate;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateCongestionPoint;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateConnectionType;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PeriodIsInRangeValidatorTest {

  private static final OffsetDateTime START_OF_RANGE = OffsetDateTime.parse("2022-11-22T00:00:00+01:00");
  private static final OffsetDateTime END_OF_RANGE = START_OF_RANGE.plusDays(2);
  private static final OffsetDateTime INFINITE = null;

  @InjectMocks
  private PeriodIsInRangeValidator testSubject;

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
    assertThat(testSubject.appliesTo(DSOPortfolioUpdate.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    // Not necessary to test with all types. Is tested on base class and by testing the map.
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> inRange() {
    FlexOrderSettlementType osSame = new FlexOrderSettlementType();
    osSame.setPeriod(START_OF_RANGE);
    FlexOrderSettlementType osPlus1 = new FlexOrderSettlementType();
    osPlus1.setPeriod(START_OF_RANGE.plusDays(1));
    FlexOrderSettlementType osPlus2 = new FlexOrderSettlementType();
    osPlus2.setPeriod(START_OF_RANGE.plusDays(2));

    ContractSettlementPeriodType cspSame = new ContractSettlementPeriodType();
    cspSame.setPeriod(START_OF_RANGE);
    ContractSettlementPeriodType cspPlus1 = new ContractSettlementPeriodType();
    cspPlus1.setPeriod(START_OF_RANGE.plusDays(1));
    ContractSettlementPeriodType cspPlus2 = new ContractSettlementPeriodType();
    cspPlus2.setPeriod(START_OF_RANGE.plusDays(2));

    ContractSettlementType cs1 = new ContractSettlementType();
    cs1.getPeriods().addAll(List.of(cspSame, cspPlus1, cspPlus2));
    ContractSettlementType cs2 = new ContractSettlementType();
    cs2.getPeriods().addAll(List.of(cspSame, cspPlus1, cspPlus2));

    FlexSettlement flexSettlementRangeWithEnd = new FlexSettlement();
    flexSettlementRangeWithEnd.setPeriodStart(START_OF_RANGE);
    flexSettlementRangeWithEnd.setPeriodEnd(END_OF_RANGE);
    flexSettlementRangeWithEnd.getFlexOrderSettlements().addAll(List.of(osSame, osPlus1, osPlus2));
    flexSettlementRangeWithEnd.getContractSettlements().addAll(List.of(cs1, cs2));

    FlexSettlement flexSettlementRangeNoEnd = new FlexSettlement();
    flexSettlementRangeNoEnd.setPeriodStart(START_OF_RANGE);
    flexSettlementRangeNoEnd.setPeriodEnd(INFINITE);
    flexSettlementRangeNoEnd.getFlexOrderSettlements().addAll(List.of(osSame, osPlus1, osPlus2));
    flexSettlementRangeNoEnd.getContractSettlements().addAll(List.of(cs1, cs2));

    FlexSettlement flexSettlementNoSubItems = new FlexSettlement();
    flexSettlementNoSubItems.setPeriodStart(START_OF_RANGE);
    flexSettlementNoSubItems.setPeriodEnd(START_OF_RANGE);

    DSOPortfolioUpdateConnectionType dsoConnSame = new DSOPortfolioUpdateConnectionType();
    dsoConnSame.setStartPeriod(START_OF_RANGE);
    dsoConnSame.setEndPeriod(END_OF_RANGE);
    DSOPortfolioUpdateConnectionType dsoConnStartPlus1 = new DSOPortfolioUpdateConnectionType();
    dsoConnStartPlus1.setStartPeriod(START_OF_RANGE.plusDays(1));
    dsoConnStartPlus1.setEndPeriod(END_OF_RANGE);
    DSOPortfolioUpdateConnectionType dsoConnStartPlus2 = new DSOPortfolioUpdateConnectionType();
    dsoConnStartPlus2.setStartPeriod(START_OF_RANGE.plusDays(2));
    dsoConnStartPlus2.setEndPeriod(END_OF_RANGE);
    DSOPortfolioUpdateConnectionType dsoConnEndMinus1 = new DSOPortfolioUpdateConnectionType();
    dsoConnEndMinus1.setStartPeriod(START_OF_RANGE);
    dsoConnEndMinus1.setEndPeriod(END_OF_RANGE.minusDays(1));
    DSOPortfolioUpdateConnectionType dsoConnEndMinus2 = new DSOPortfolioUpdateConnectionType();
    dsoConnEndMinus2.setStartPeriod(START_OF_RANGE);
    dsoConnEndMinus2.setEndPeriod(END_OF_RANGE.minusDays(2));
    DSOPortfolioUpdateConnectionType dsoConnEndInfinite = new DSOPortfolioUpdateConnectionType();
    dsoConnEndInfinite.setStartPeriod(START_OF_RANGE);
    dsoConnEndInfinite.setEndPeriod(INFINITE);

    DSOPortfolioUpdateCongestionPoint dsoCpWithEnd = new DSOPortfolioUpdateCongestionPoint();
    dsoCpWithEnd.setStartPeriod(START_OF_RANGE);
    dsoCpWithEnd.setEndPeriod(END_OF_RANGE);
    dsoCpWithEnd.getConnections().addAll(List.of(dsoConnSame, dsoConnStartPlus1, dsoConnStartPlus2, dsoConnEndMinus1, dsoConnEndMinus2));
    DSOPortfolioUpdateCongestionPoint dsoCpNoEnd = new DSOPortfolioUpdateCongestionPoint();
    dsoCpNoEnd.setStartPeriod(START_OF_RANGE);
    dsoCpNoEnd.setEndPeriod(INFINITE);
    dsoCpNoEnd.getConnections().addAll(List.of(dsoConnSame, dsoConnStartPlus1, dsoConnStartPlus2, dsoConnEndMinus1, dsoConnEndMinus2, dsoConnEndInfinite));
    DSOPortfolioUpdateCongestionPoint dsoCpNoSubItems = new DSOPortfolioUpdateCongestionPoint();
    dsoCpNoSubItems.setStartPeriod(START_OF_RANGE);
    dsoCpNoSubItems.setEndPeriod(START_OF_RANGE);

    DSOPortfolioUpdate dsoUpdateWithEnd = new DSOPortfolioUpdate();
    dsoUpdateWithEnd.getCongestionPoints().add(dsoCpWithEnd);

    DSOPortfolioUpdate dsoUpdateNoEnd = new DSOPortfolioUpdate();
    dsoUpdateNoEnd.getCongestionPoints().add(dsoCpNoEnd);

    DSOPortfolioUpdate dsoUpdateNoSubItems = new DSOPortfolioUpdate();
    dsoUpdateNoSubItems.getCongestionPoints().add(dsoCpNoSubItems);

    DSOPortfolioUpdate dsoUpdateNoSubItemsAtAll = new DSOPortfolioUpdate();

    return Stream.of(
        Arguments.of(flexSettlementRangeWithEnd),
        Arguments.of(flexSettlementRangeNoEnd),
        Arguments.of(flexSettlementNoSubItems),
        Arguments.of(dsoUpdateWithEnd),
        Arguments.of(dsoUpdateNoEnd),
        Arguments.of(dsoUpdateNoSubItems),
        Arguments.of(dsoUpdateNoSubItemsAtAll)
    );
  }

  @ParameterizedTest
  @MethodSource("inRange")
  void valid_allInRange(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(sender, payloadMessage)).isTrue();
  }

  public static Stream<Arguments> notInRange() {
    FlexOrderSettlementType osSame = new FlexOrderSettlementType();
    osSame.setPeriod(START_OF_RANGE);
    FlexOrderSettlementType osBefore = new FlexOrderSettlementType();
    osBefore.setPeriod(START_OF_RANGE.minusDays(1));
    FlexOrderSettlementType osAfter = new FlexOrderSettlementType();
    osBefore.setPeriod(END_OF_RANGE.plusDays(1));

    ContractSettlementPeriodType cspSame = new ContractSettlementPeriodType();
    cspSame.setPeriod(START_OF_RANGE);
    ContractSettlementPeriodType cspBefore = new ContractSettlementPeriodType();
    cspBefore.setPeriod(START_OF_RANGE.minusDays(1));
    ContractSettlementPeriodType cspAfter = new ContractSettlementPeriodType();
    cspBefore.setPeriod(END_OF_RANGE.plusDays(1));

    ContractSettlementType csBefore = new ContractSettlementType();
    csBefore.getPeriods().addAll(List.of(cspSame, cspBefore));
    ContractSettlementType csAfter = new ContractSettlementType();
    csAfter.getPeriods().addAll(List.of(cspSame, cspAfter));

    FlexSettlement flexSettlementOsBefore = new FlexSettlement();
    flexSettlementOsBefore.setPeriodStart(START_OF_RANGE);
    flexSettlementOsBefore.setPeriodEnd(END_OF_RANGE);
    flexSettlementOsBefore.getFlexOrderSettlements().addAll(List.of(osSame, osBefore));

    FlexSettlement flexSettlementOsAfter = new FlexSettlement();
    flexSettlementOsAfter.setPeriodStart(START_OF_RANGE);
    flexSettlementOsAfter.setPeriodEnd(END_OF_RANGE);
    flexSettlementOsAfter.getFlexOrderSettlements().addAll(List.of(osSame, osAfter));

    FlexSettlement flexSettlementCsBefore = new FlexSettlement();
    flexSettlementCsBefore.setPeriodStart(START_OF_RANGE);
    flexSettlementCsBefore.setPeriodEnd(END_OF_RANGE);
    flexSettlementCsBefore.getContractSettlements().add(csBefore);

    FlexSettlement flexSettlementCsAfter = new FlexSettlement();
    flexSettlementCsAfter.setPeriodStart(START_OF_RANGE);
    flexSettlementCsAfter.setPeriodEnd(END_OF_RANGE);
    flexSettlementCsAfter.getContractSettlements().add(csAfter);

    DSOPortfolioUpdateConnectionType dsoConnSame = new DSOPortfolioUpdateConnectionType();
    dsoConnSame.setStartPeriod(START_OF_RANGE);
    dsoConnSame.setEndPeriod(END_OF_RANGE);
    DSOPortfolioUpdateConnectionType dsoConnStartBefore = new DSOPortfolioUpdateConnectionType();
    dsoConnStartBefore.setStartPeriod(START_OF_RANGE.minusDays(1));
    dsoConnStartBefore.setEndPeriod(END_OF_RANGE);
    DSOPortfolioUpdateConnectionType dsoConnEndBefore = new DSOPortfolioUpdateConnectionType();
    dsoConnEndBefore.setStartPeriod(START_OF_RANGE);
    dsoConnEndBefore.setEndPeriod(START_OF_RANGE.minusDays(1));
    DSOPortfolioUpdateConnectionType dsoConnStartAfter = new DSOPortfolioUpdateConnectionType();
    dsoConnStartAfter.setStartPeriod(END_OF_RANGE.plusDays(1));
    dsoConnStartAfter.setEndPeriod(END_OF_RANGE);
    DSOPortfolioUpdateConnectionType dsoConnEndAfter = new DSOPortfolioUpdateConnectionType();
    dsoConnEndAfter.setStartPeriod(START_OF_RANGE);
    dsoConnEndAfter.setEndPeriod(END_OF_RANGE.plusDays(1));

    DSOPortfolioUpdateCongestionPoint dsoCpStartBefore = new DSOPortfolioUpdateCongestionPoint();
    dsoCpStartBefore.setStartPeriod(START_OF_RANGE);
    dsoCpStartBefore.setEndPeriod(END_OF_RANGE);
    dsoCpStartBefore.getConnections().addAll(List.of(dsoConnSame, dsoConnStartBefore));
    DSOPortfolioUpdateCongestionPoint dsoCpStartAfter = new DSOPortfolioUpdateCongestionPoint();
    dsoCpStartAfter.setStartPeriod(START_OF_RANGE);
    dsoCpStartAfter.setEndPeriod(END_OF_RANGE);
    dsoCpStartAfter.getConnections().addAll(List.of(dsoConnSame, dsoConnStartAfter));
    DSOPortfolioUpdateCongestionPoint dsoCpEndBefore = new DSOPortfolioUpdateCongestionPoint();
    dsoCpEndBefore.setStartPeriod(START_OF_RANGE);
    dsoCpEndBefore.setEndPeriod(END_OF_RANGE);
    dsoCpEndBefore.getConnections().addAll(List.of(dsoConnSame, dsoConnEndBefore));
    DSOPortfolioUpdateCongestionPoint dsoCpEndAfter = new DSOPortfolioUpdateCongestionPoint();
    dsoCpEndAfter.setStartPeriod(START_OF_RANGE);
    dsoCpEndAfter.setEndPeriod(END_OF_RANGE);
    dsoCpEndAfter.getConnections().addAll(List.of(dsoConnSame, dsoConnEndAfter));

    DSOPortfolioUpdate dsoUpdateStartBefore = new DSOPortfolioUpdate();
    dsoUpdateStartBefore.getCongestionPoints().add(dsoCpStartBefore);

    DSOPortfolioUpdate dsoUpdateStartAfter = new DSOPortfolioUpdate();
    dsoUpdateStartAfter.getCongestionPoints().add(dsoCpStartAfter);

    DSOPortfolioUpdate dsoUpdateEndBefore = new DSOPortfolioUpdate();
    dsoUpdateEndBefore.getCongestionPoints().add(dsoCpEndBefore);

    DSOPortfolioUpdate dsoUpdateEndAfter = new DSOPortfolioUpdate();
    dsoUpdateEndAfter.getCongestionPoints().add(dsoCpEndAfter);

    return Stream.of(
        Arguments.of(flexSettlementOsBefore),
        Arguments.of(flexSettlementOsAfter),
        Arguments.of(flexSettlementCsBefore),
        Arguments.of(flexSettlementCsAfter),
        Arguments.of(dsoUpdateStartBefore),
        Arguments.of(dsoUpdateStartAfter),
        Arguments.of(dsoUpdateEndBefore),
        Arguments.of(dsoUpdateEndAfter)
    );
  }

  @ParameterizedTest
  @MethodSource("notInRange")
  void valid_false_notInRange(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(sender, payloadMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Period out of bounds");
  }
}