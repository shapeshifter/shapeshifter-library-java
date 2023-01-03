package org.lfenergy.shapeshifter.connector.service.validation.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.model.UftpRoleInformation;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderRoleMatchesContentTypeValidator implements UftpBaseValidator<PayloadMessageType> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return true;
  }

  @Override
  public boolean valid(UftpParticipant sender, PayloadMessageType payloadMessage) {
    return contentMatchesRole(sender, payloadMessage);
  }

  private boolean contentMatchesRole(UftpParticipant sender, PayloadMessageType payloadMessage) {
    return UftpRoleInformation.getMessageTypes(sender.role())
                              .contains(payloadMessage.getClass());
  }

  @Override
  public String getReason() {
    return "Invalid Sender (not matching role)";
  }
}
