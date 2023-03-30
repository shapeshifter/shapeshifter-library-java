// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.connector.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;
import static org.lfenergy.shapeshifter.connector.service.validation.tools.SetOf.setOfNullable;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferencedBaselineReferenceValidator implements UftpValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;

  private final PayloadMessagePropertyRetriever<PayloadMessageType, Set<String>> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexOffer.class, m -> setOfNullable(((FlexOffer) m).getBaselineReference()),
          FlexOrder.class, m -> setOfNullable(((FlexOrder) m).getBaselineReference()),
          FlexSettlement.class, m -> collectBaselineReferences((FlexSettlement) m)
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.isTypeInMap(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    var value = retriever.getProperty(uftpMessage.payloadMessage());
    return value.isEmpty() || value.stream().allMatch(support::isValidBaselineReference);
  }

  @Override
  public String getReason() {
    return "Unknown reference BaselineReference";
  }

  private Set<String> collectBaselineReferences(FlexSettlement m) {
    return m.getFlexOrderSettlements().stream()
            .map(FlexOrderSettlementType::getBaselineReference)
            .collect(toSetIgnoreNulls());
  }
}
