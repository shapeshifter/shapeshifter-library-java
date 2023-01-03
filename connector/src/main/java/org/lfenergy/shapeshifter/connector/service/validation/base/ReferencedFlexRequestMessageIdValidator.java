package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
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
  public boolean valid(UftpParticipant sender, FlexOffer payloadMessage) {
    var value = Optional.ofNullable(payloadMessage.getFlexRequestMessageID());
    return value.isEmpty() || support.getPreviousMessage(value.get(), FlexRequest.class).isPresent();
  }

  @Override
  public String getReason() {
    return "Unknown reference FlexRequestMessageID";
  }
}
