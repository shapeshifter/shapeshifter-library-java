package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.connector.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementStatusType;
import org.lfenergy.shapeshifter.api.FlexSettlementResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferencedFlexSettlementResponseOrderReferenceValidator implements UftpBaseValidator<FlexSettlementResponse> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexSettlementResponse.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean valid(UftpParticipant sender, FlexSettlementResponse payloadMessage) {
    var orderReferences = collectOrderReferences(payloadMessage);
    return orderReferences.isEmpty() || orderReferences.stream().allMatch(orderReference -> support.isValidOrderReference(orderReference, payloadMessage.getRecipientDomain()));
  }

  @Override
  public String getReason() {
    return "Unknown reference OrderReference in FlexSettlementResponse";
  }

  private Set<String> collectOrderReferences(FlexSettlementResponse m) {
    return m.getFlexOrderSettlementStatuses().stream()
            .map(FlexOrderSettlementStatusType::getOrderReference)
            .collect(toSetIgnoreNulls());
  }
}
