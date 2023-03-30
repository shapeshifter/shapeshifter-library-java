package org.lfenergy.shapeshifter.connector.service.validation.base;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.model.UftpRequestResponseMapping;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferencedRequestMessageIdInResponseValidator implements UftpValidator<PayloadMessageResponseType> {

  private final UftpMessageSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return UftpRequestResponseMapping.hasReferencedRequestMessageId(clazz);
  }

  @Override
  public int order() {
    return 0;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageResponseType> message) {
    var value = UftpRequestResponseMapping.getReferencedRequestMessageId(message.payloadMessage());
    return value.isEmpty() || support.getPreviousMessage(value.get(), message.payloadMessage().getRecipientDomain()).isPresent();
  }

  @Override
  public String getReason() {
    return "Unknown reference Request message ID";
  }
}
