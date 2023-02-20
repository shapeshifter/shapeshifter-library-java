package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.DUPLICATE_MESSAGE;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.NEW_MESSAGE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateIdentifierValidator implements UftpBaseValidator<PayloadMessageType> {

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
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
    var result = duplicateDetection.isDuplicate(uftpMessage.payloadMessage());
    return NEW_MESSAGE == result || DUPLICATE_MESSAGE == result;
  }

  @Override
  public String getReason() {
    return "Duplicate Identifier";
  }
}
