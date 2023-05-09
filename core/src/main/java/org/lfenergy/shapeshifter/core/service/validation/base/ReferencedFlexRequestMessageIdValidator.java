// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;


@RequiredArgsConstructor
public class ReferencedFlexRequestMessageIdValidator implements UftpValidator<FlexOffer> {

  private final UftpMessageSupport messageSupport;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexOffer.class.isAssignableFrom(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean isValid(UftpMessage<FlexOffer> uftpMessage) {
    var value = Optional.ofNullable(uftpMessage.payloadMessage().getFlexRequestMessageID());
    return value.isEmpty() || messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(value.get(), FlexRequest.class)).isPresent();
  }

  @Override
  public String getReason() {
    return "Unknown reference FlexRequestMessageID";
  }
}
