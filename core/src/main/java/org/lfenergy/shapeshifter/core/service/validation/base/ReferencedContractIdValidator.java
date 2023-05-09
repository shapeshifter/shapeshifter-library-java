// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.lfenergy.shapeshifter.core.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;
import static org.lfenergy.shapeshifter.core.service.validation.tools.SetOf.setOfNullable;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.ContractSettlementType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.ContractSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.tools.PayloadMessagePropertyRetriever;


@RequiredArgsConstructor
public class ReferencedContractIdValidator implements UftpValidator<PayloadMessageType> {

  private final ContractSupport contractSupport;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, Set<String>> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexRequest.class, m -> setOfNullable(((FlexRequest) m).getContractID()),
          FlexOffer.class, m -> setOfNullable(((FlexOffer) m).getContractID()),
          FlexOrder.class, m -> setOfNullable(((FlexOrder) m).getContractID()),
          FlexReservationUpdate.class, m -> setOfNullable(((FlexReservationUpdate) m).getContractID()),
          FlexSettlement.class, m -> collectContractIds((FlexSettlement) m)
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
    var value = retriever.getProperty(uftpMessage.payloadMessage());
    return value.isEmpty() || value.stream().allMatch(contractSupport::isSupportedContractID);
  }

  @Override
  public String getReason() {
    return "Unknown reference ContractID";
  }

  private Set<String> collectContractIds(FlexSettlement m) {
    return Stream.concat(
        m.getFlexOrderSettlements().stream().map(FlexOrderSettlementType::getContractID),
        m.getContractSettlements().stream().map(ContractSettlementType::getContractID)
    ).collect(toSetIgnoreNulls());
  }
}
