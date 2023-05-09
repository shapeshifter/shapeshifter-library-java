// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.lfenergy.shapeshifter.core.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;
import static org.lfenergy.shapeshifter.core.service.validation.tools.SetOf.setOfNullable;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.DPrognosis;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.tools.PayloadMessagePropertyRetriever;


@RequiredArgsConstructor
public class ReferencedDPrognosisMessageIdValidator implements UftpValidator<PayloadMessageType> {

  private final UftpMessageSupport messageSupport;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, Set<String>> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexOffer.class, m -> setOfNullable(((FlexOffer) m).getDPrognosisMessageID()),
          FlexOrder.class, m -> setOfNullable(((FlexOrder) m).getDPrognosisMessageID()),
          FlexSettlement.class, m -> collectDPrognosisMessageId((FlexSettlement) m)
      )
  );

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.isTypeInMap(clazz);
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    var value = retriever.getProperty(uftpMessage.payloadMessage());
    return value.isEmpty() || value.stream().allMatch(
        msgId -> messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(msgId, DPrognosis.class)).isPresent()
    );
  }

  @Override
  public String getReason() {
    return "Unknown reference D-PrognosisMessageID";
  }

  private Set<String> collectDPrognosisMessageId(FlexSettlement m) {
    return m.getFlexOrderSettlements().stream()
            .map(FlexOrderSettlementType::getDPrognosisMessageID)
            .collect(toSetIgnoreNulls());
  }
}
