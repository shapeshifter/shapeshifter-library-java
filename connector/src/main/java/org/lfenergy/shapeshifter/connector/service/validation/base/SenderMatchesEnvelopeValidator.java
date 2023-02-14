package org.lfenergy.shapeshifter.connector.service.validation.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderMatchesEnvelopeValidator implements UftpBaseValidator<PayloadMessageType> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return true;
  }

  @Override
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
    return sameSender(uftpMessage.sender(), uftpMessage.payloadMessage());
  }

  private boolean sameSender(UftpParticipant sender, PayloadMessageType payloadMessage) {
    return sender.domain().equals(payloadMessage.getSenderDomain());
  }

  @Override
  public String getReason() {
    return "Invalid Sender (not matching envelope)";
  }
}
