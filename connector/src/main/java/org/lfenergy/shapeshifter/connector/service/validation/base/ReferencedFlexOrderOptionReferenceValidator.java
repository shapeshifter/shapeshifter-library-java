package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.util.Optional;
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
public class ReferencedFlexOrderOptionReferenceValidator implements UftpBaseValidator<FlexOrder> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexOrder.class.equals(clazz);
  }

  @Override
  public boolean valid(UftpParticipant sender, FlexOrder payloadMessage) {
    var value = Optional.ofNullable(payloadMessage.getOptionReference());
    return value.isEmpty() || support.isValidOfferOptionReference(payloadMessage.getFlexOfferMessageID(), value.get());
  }

  @Override
  public String getReason() {
    return "Unknown reference OptionReference in FlexOrder";
  }
}
