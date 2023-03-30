// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.connector.service.validation.tools.DateTimeCompareAllowingInfinite.equalOrAfter;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.ContractSettlementType;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdate;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateCongestionPoint;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateConnectionType;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodIsInRangeValidator implements UftpValidator<PayloadMessageType> {

  private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexSettlement.class, m -> periodsAreInRange((FlexSettlement) m),
          DSOPortfolioUpdate.class, m -> subRangesAreInRange((DSOPortfolioUpdate) m)
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.isTypeInMap(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    return retriever.getProperty(uftpMessage.payloadMessage());
  }

  @Override
  public String getReason() {
    return "Period out of bounds";
  }

  private boolean periodsAreInRange(FlexSettlement msg) {
    var start = msg.getPeriodStart();
    var end = msg.getPeriodEnd();
    var resulOrders = msg.getFlexOrderSettlements()
                         .stream()
                         .allMatch(s -> valueIsInRange(start, end, s.getPeriod()));
    var resultContracts = msg.getContractSettlements()
                             .stream()
                             .map(ContractSettlementType::getPeriods)
                             .flatMap(Collection::stream)
                             .allMatch(s -> valueIsInRange(start, end, s.getPeriod()));
    return resulOrders && resultContracts;
  }

  private boolean subRangesAreInRange(DSOPortfolioUpdate msg) {
    return msg.getCongestionPoints().stream().allMatch(this::subRangesAreInRange);
  }

  private boolean subRangesAreInRange(DSOPortfolioUpdateCongestionPoint cp) {
    var start = cp.getStartPeriod();
    var end = cp.getEndPeriod();
    return cp.getConnections().stream().allMatch(conn -> subRangeIsInRange(start, end, conn));
  }

  private boolean subRangeIsInRange(OffsetDateTime start, OffsetDateTime end, DSOPortfolioUpdateConnectionType conn) {
    return valueIsInRange(start, end, conn.getStartPeriod())
        && valueIsInRange(start, end, conn.getEndPeriod());
  }

  private boolean valueIsInRange(OffsetDateTime start, OffsetDateTime end, OffsetDateTime value) {
    return equalOrAfter(value, start) && equalOrAfter(end, value);
  }
}
