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
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodStartBeforeOrEqualsToEndValidator implements UftpBaseValidator<PayloadMessageType> {

  private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexSettlement.class, m -> startBeforeOrEqualsToEnd((FlexSettlement) m),
          AGRPortfolioUpdate.class, m -> startBeforeOrEqualsToEnd((AGRPortfolioUpdate) m),
          DSOPortfolioUpdate.class, m -> startBeforeOrEqualsToEnd((DSOPortfolioUpdate) m)
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.typeInMap(clazz);
  }

  @Override
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
    return retriever.getProperty(uftpMessage.payloadMessage());
  }

  @Override
  public String getReason() {
    return "Period out of bounds";
  }

  private boolean startBeforeOrEqualsToEnd(FlexSettlement msg) {
    return startBeforeOrEqualsToEnd(msg.getPeriodStart(), msg.getPeriodEnd());
  }

  private boolean startBeforeOrEqualsToEnd(AGRPortfolioUpdate msg) {
    return msg.getConnections().stream().allMatch(conn -> startBeforeOrEqualsToEnd(conn.getStartPeriod(), conn.getEndPeriod()));
  }

  private boolean startBeforeOrEqualsToEnd(DSOPortfolioUpdate msg) {
    return msg.getCongestionPoints().stream().allMatch(this::startBeforeOrEqualsToEnd);
  }

  private boolean startBeforeOrEqualsToEnd(DSOPortfolioUpdateCongestionPoint cp) {
    return startBeforeOrEqualsToEnd(cp.getStartPeriod(), cp.getEndPeriod()) &&
        cp.getConnections().stream().allMatch(conn -> startBeforeOrEqualsToEnd(conn.getStartPeriod(), conn.getEndPeriod()));
  }

  private boolean startBeforeOrEqualsToEnd(OffsetDateTime periodStart, OffsetDateTime periodEnd) {
    if (periodStart == null) {
      return false; // start is mandatory
    }
    return equalOrAfter(periodEnd, periodStart);
  }
}
