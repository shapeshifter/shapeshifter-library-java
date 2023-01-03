package org.lfenergy.shapeshifter.connector.service.validation.message;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Service;


/**
 * Validates that a flex order is linked to a flex offer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlexOrderActiveFlexOfferValidator implements UftpMessageValidator<FlexOrder> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOrder.class);
  }

  @Override
  public boolean valid(UftpParticipant sender, FlexOrder flexOrder) {
    if (isEmpty(flexOrder.getFlexOfferMessageID())) {
      return false;
    }
    var flexOffer = support.getPreviousMessage(flexOrder.getFlexOfferMessageID(), FlexOffer.class);
    return flexOffer.filter(offer -> flexOfferIsActive(offer.getExpirationDateTime())).isPresent();
  }

  private boolean flexOfferIsActive(OffsetDateTime dateTime) {
    return dateTime.isAfter(OffsetDateTime.now());
  }

  @Override
  public String getReason() {
    return "Flex Order is not related to an active Flex Offer";
  }
}
