package org.lfenergy.shapeshifter.connector.service.validation.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlexOrderOfferIsNotRevokedValidator implements UftpBaseValidator<FlexOrder> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexOrder.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean valid(UftpMessage<FlexOrder> uftpMessage) {
    return !support.existsFlexRevocation(uftpMessage.payloadMessage().getFlexOfferMessageID(), uftpMessage.payloadMessage().getRecipientDomain());
  }

  @Override
  public String getReason() {
    return "Reference message revoked";
  }
}
