package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.connector.model.UftpRoleInformation.getRecipientRoleBySenderRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipientValidator implements UftpBaseValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return true;
  }

  @Override
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
    return support.isHandledRecipient(uftpMessage.payloadMessage().getRecipientDomain(), getRecipientRoleBySenderRole(uftpMessage.sender().role()));
  }

  @Override
  public String getReason() {
    return "Unknown Recipient";
  }
}
