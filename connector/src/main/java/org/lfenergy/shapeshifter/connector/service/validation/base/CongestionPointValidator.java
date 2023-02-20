package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.connector.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;
import static org.lfenergy.shapeshifter.connector.service.validation.tools.SetOf.setOfNullable;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CongestionPointValidator implements UftpBaseValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, Set<String>> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexMessageType.class, m -> setOfNullable(((FlexMessageType) m).getCongestionPoint()),
          FlexSettlement.class, m -> collectCongestionPoints((FlexSettlement) m)
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.typeInMap(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
    var value = retriever.getProperty(uftpMessage.payloadMessage());
    return value.isEmpty() || support.areKnownCongestionPoints(value);
  }

  @Override
  public String getReason() {
    return "Invalid congestion point";
  }

  private Set<String> collectCongestionPoints(FlexSettlement m) {
    return m.getFlexOrderSettlements().stream()
            .map(FlexOrderSettlementType::getCongestionPoint)
            .collect(toSetIgnoreNulls());
  }

}
