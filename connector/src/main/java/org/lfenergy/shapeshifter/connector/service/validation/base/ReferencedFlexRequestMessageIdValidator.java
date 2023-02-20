package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferencedFlexRequestMessageIdValidator implements UftpBaseValidator<FlexOffer> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexOffer.class.isAssignableFrom(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean valid(UftpMessage<FlexOffer> uftpMessage) {
    var value = Optional.ofNullable(uftpMessage.payloadMessage().getFlexRequestMessageID());
    return value.isEmpty() || support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(value.get(), FlexRequest.class)).isPresent();
  }

  @Override
  public String getReason() {
    return "Unknown reference FlexRequestMessageID";
  }
}
