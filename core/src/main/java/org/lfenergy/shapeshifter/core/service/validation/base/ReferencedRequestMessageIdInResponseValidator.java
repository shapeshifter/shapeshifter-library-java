package org.lfenergy.shapeshifter.core.service.validation.base;


import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpRequestResponseMapping;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;


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
    return value.isEmpty() || support.findDuplicateMessage(value.get(), message.payloadMessage().getSenderDomain() ,
            message.payloadMessage().getRecipientDomain()).isPresent();
  }

  @Override
  public String getReason() {
    return "Unknown reference Request message ID";
  }
}
