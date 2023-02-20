package org.lfenergy.shapeshifter.connector.service.validation.message;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

/**
 * Validates that at the price of the order matches the price in the offer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlexOrderPriceMatchValidator implements UftpMessageValidator<FlexOrder> {

  private final UftpValidatorSupport uftpValidatorSupport;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOrder.class);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean valid(UftpMessage<FlexOrder> uftpMessage) {
    var flexOrder = uftpMessage.payloadMessage();
    var flexOffer = uftpValidatorSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexOrder.getFlexOfferMessageID(), FlexOffer.class));
    return flexOffer.map(offer -> offer.getOfferOptions().stream().allMatch(it -> priceMatches(it, flexOrder.getPrice()))).orElse(false);
  }

  private boolean priceMatches(FlexOfferOptionType flexOfferOption, BigDecimal orderPrice) {
    return flexOfferOption.getPrice().equals(orderPrice);
  }

  @Override
  public String getReason() {
    return "Price in the order does not match the price given in the offer";
  }
}
