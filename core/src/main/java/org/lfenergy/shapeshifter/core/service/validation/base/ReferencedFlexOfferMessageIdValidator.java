// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferRevocation;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.tools.PayloadMessagePropertyRetriever;


@RequiredArgsConstructor
public class ReferencedFlexOfferMessageIdValidator implements UftpValidator<PayloadMessageType> {

  private final UftpMessageSupport messageSupport;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, String> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexOfferRevocation.class, m -> ((FlexOfferRevocation) m).getFlexOfferMessageID(),
          FlexOrder.class, m -> ((FlexOrder) m).getFlexOfferMessageID()
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
    var value = retriever.getOptionalProperty(uftpMessage.payloadMessage());
    return value.isEmpty() || messageSupport.getPreviousMessage(uftpMessage.payloadMessage().getConversationID(),
            uftpMessage.referenceToPreviousMessage(value.get(), uftpMessage.payloadMessage().getConversationID(),
            FlexOffer.class)).isPresent();
  }

  @Override
  public String getReason() {
    return "Unknown reference FlexOfferMessageID";
  }
}
