package org.lfenergy.shapeshifter.connector.service.validation.tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexSettlement;

class NullablesToLinkedSetTest {

  private static final String CONGESTION_POINT1 = "CONGESTION_POINT1";
  private static final String CONGESTION_POINT2 = "CONGESTION_POINT2";
  private static final String CONGESTION_POINT3 = "CONGESTION_POINT3";

  private Set<String> collect(FlexSettlement testItem) {
    return testItem.getFlexOrderSettlements()
                   .stream()
                   .map(FlexOrderSettlementType::getCongestionPoint)
                   .collect(toSetIgnoreNulls());
  }

  private Set<String> collectParallel(FlexSettlement testItem) {
    return testItem.getFlexOrderSettlements()
                   .parallelStream()
                   .map(FlexOrderSettlementType::getCongestionPoint)
                   .collect(toSetIgnoreNulls());
  }

  @Test
  void toSetIgnoreNulls_emptyList() {
    FlexSettlement testItem = new FlexSettlement();

    var actual = collect(testItem);

    assertThat(actual).isEmpty();
  }

  @Test
  void toSetIgnoreNulls_listWithOnlyNullObjects() {
    FlexSettlement testItem = new FlexSettlement();
    testItem.getFlexOrderSettlements().addAll(
        List.of(new FlexOrderSettlementType(), new FlexOrderSettlementType())
    );

    var actual = collect(testItem);

    assertThat(actual).isEmpty();
  }

  @Test
  void toSetIgnoreNulls_listWithSomeValidObjectsAndNullObjects() {
    var v1 = new FlexOrderSettlementType();
    v1.setCongestionPoint(CONGESTION_POINT1);
    var v2 = new FlexOrderSettlementType();
    v2.setCongestionPoint(CONGESTION_POINT2);
    var v3 = new FlexOrderSettlementType();
    v3.setCongestionPoint(CONGESTION_POINT3);

    FlexSettlement testItem = new FlexSettlement();
    testItem.getFlexOrderSettlements().addAll(List.of(
        v1, new FlexOrderSettlementType(), v2, new FlexOrderSettlementType(), v3
    ));

    var actual = collect(testItem);

    assertThat(actual).containsExactly(CONGESTION_POINT1, CONGESTION_POINT2, CONGESTION_POINT3);
  }

  @Test
  void toSetIgnoreNulls_listWithValidObjectsOnly() {
    var v1 = new FlexOrderSettlementType();
    v1.setCongestionPoint(CONGESTION_POINT1);
    var v2 = new FlexOrderSettlementType();
    v2.setCongestionPoint(CONGESTION_POINT2);
    var v3 = new FlexOrderSettlementType();
    v3.setCongestionPoint(CONGESTION_POINT3);

    FlexSettlement testItem = new FlexSettlement();
    testItem.getFlexOrderSettlements().addAll(List.of(v1, v2, v3));

    var actual = collect(testItem);

    assertThat(actual).containsExactly(CONGESTION_POINT1, CONGESTION_POINT2, CONGESTION_POINT3);
  }

  @Test
  void toSetIgnoreNulls_emptyList_parallel() {
    FlexSettlement testItem = new FlexSettlement();

    var actual = collectParallel(testItem);

    assertThat(actual).isEmpty();
  }

  @Test
  void toSetIgnoreNulls_listWithOnlyNullObjects_parallel() {
    FlexSettlement testItem = new FlexSettlement();
    testItem.getFlexOrderSettlements().addAll(
        List.of(new FlexOrderSettlementType(), new FlexOrderSettlementType())
    );

    var actual = collectParallel(testItem);

    assertThat(actual).isEmpty();
  }

  @Test
  void toSetIgnoreNulls_listWithSomeValidObjectsAndNullObjects_parallel() {
    var v1 = new FlexOrderSettlementType();
    v1.setCongestionPoint(CONGESTION_POINT1);
    var v2 = new FlexOrderSettlementType();
    v2.setCongestionPoint(CONGESTION_POINT2);
    var v3 = new FlexOrderSettlementType();
    v3.setCongestionPoint(CONGESTION_POINT3);

    FlexSettlement testItem = new FlexSettlement();
    testItem.getFlexOrderSettlements().addAll(List.of(
        v1, new FlexOrderSettlementType(), v2, new FlexOrderSettlementType(), v3
    ));

    var actual = collectParallel(testItem);

    assertThat(actual).containsExactly(CONGESTION_POINT1, CONGESTION_POINT2, CONGESTION_POINT3);
  }

  @Test
  void toSetIgnoreNulls_listWithValidObjectsOnly_parallel() {
    var v1 = new FlexOrderSettlementType();
    v1.setCongestionPoint(CONGESTION_POINT1);
    var v2 = new FlexOrderSettlementType();
    v2.setCongestionPoint(CONGESTION_POINT2);
    var v3 = new FlexOrderSettlementType();
    v3.setCongestionPoint(CONGESTION_POINT3);

    FlexSettlement testItem = new FlexSettlement();
    testItem.getFlexOrderSettlements().addAll(List.of(v1, v2, v3));

    var actual = collectParallel(testItem);

    assertThat(actual).containsExactly(CONGESTION_POINT1, CONGESTION_POINT2, CONGESTION_POINT3);
  }
}