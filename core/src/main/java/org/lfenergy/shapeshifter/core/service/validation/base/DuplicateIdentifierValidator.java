// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.lfenergy.shapeshifter.core.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.DUPLICATE_MESSAGE;
import static org.lfenergy.shapeshifter.core.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.NEW_MESSAGE;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.receiving.DuplicateMessageDetection;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;


@RequiredArgsConstructor
public class DuplicateIdentifierValidator implements UftpValidator<PayloadMessageType> {

  private final DuplicateMessageDetection duplicateDetection;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return true;
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_BASE;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    var result = duplicateDetection.isDuplicate(uftpMessage.payloadMessage());
    return NEW_MESSAGE == result || DUPLICATE_MESSAGE == result;
  }

  @Override
  public String getReason() {
    return "Duplicate Identifier";
  }
}
