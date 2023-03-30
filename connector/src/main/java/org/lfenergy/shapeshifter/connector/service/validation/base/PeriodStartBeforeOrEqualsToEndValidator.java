// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.connector.service.validation.tools.DateTimeCompareAllowingInfinite.equalOrAfter;

import java.time.OffsetDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.AGRPortfolioUpdate;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdate;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateCongestionPoint;
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
public class PeriodStartBeforeOrEqualsToEndValidator implements UftpValidator<PayloadMessageType> {

  private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexSettlement.class, m -> isStartBeforeOrEqualsToEnd((FlexSettlement) m),
          AGRPortfolioUpdate.class, m -> isStartBeforeOrEqualsToEnd((AGRPortfolioUpdate) m),
          DSOPortfolioUpdate.class, m -> isStartBeforeOrEqualsToEnd((DSOPortfolioUpdate) m)
      )
  );

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.isTypeInMap(clazz);
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    return retriever.getProperty(uftpMessage.payloadMessage());
  }

  @Override
  public String getReason() {
    return "Period out of bounds";
  }

  private boolean isStartBeforeOrEqualsToEnd(FlexSettlement msg) {
    return isStartBeforeOrEqualsToEnd(msg.getPeriodStart(), msg.getPeriodEnd());
  }

  private boolean isStartBeforeOrEqualsToEnd(AGRPortfolioUpdate msg) {
    return msg.getConnections().stream().allMatch(conn -> isStartBeforeOrEqualsToEnd(conn.getStartPeriod(), conn.getEndPeriod()));
  }

  private boolean isStartBeforeOrEqualsToEnd(DSOPortfolioUpdate msg) {
    return msg.getCongestionPoints().stream().allMatch(this::isStartBeforeOrEqualsToEnd);
  }

  private boolean isStartBeforeOrEqualsToEnd(DSOPortfolioUpdateCongestionPoint cp) {
    return isStartBeforeOrEqualsToEnd(cp.getStartPeriod(), cp.getEndPeriod()) &&
        cp.getConnections().stream().allMatch(conn -> isStartBeforeOrEqualsToEnd(conn.getStartPeriod(), conn.getEndPeriod()));
  }

  private boolean isStartBeforeOrEqualsToEnd(OffsetDateTime periodStart, OffsetDateTime periodEnd) {
    if (periodStart == null) {
      return false; // start is mandatory
    }
    return equalOrAfter(periodEnd, periodStart);
  }
}
