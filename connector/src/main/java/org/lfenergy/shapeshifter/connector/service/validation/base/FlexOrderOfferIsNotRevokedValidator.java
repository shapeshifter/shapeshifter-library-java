package org.lfenergy.shapeshifter.connector.service.validation.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
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
  public boolean valid(UftpParticipant sender, FlexOrder payloadMessage) {
    return !support.existsFlexRevocation(payloadMessage.getFlexOfferMessageID(), payloadMessage.getRecipientDomain());
  }

  @Override
  public String getReason() {
    return "Reference message revoked";
  }
}
