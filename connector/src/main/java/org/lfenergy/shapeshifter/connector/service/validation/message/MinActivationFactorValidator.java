package org.lfenergy.shapeshifter.connector.service.validation.message;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageValidator;
import org.springframework.stereotype.Service;

/**
 * Validates that the min activation factor (if present) is in the interval <0,1] (thus: > 0, <=1)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinActivationFactorValidator implements UftpMessageValidator<FlexOffer> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOffer.class);
  }

  @Override
  public boolean valid(UftpParticipant sender, FlexOffer flexOffer) {
    return flexOffer.getOfferOptions().stream().allMatch(
        it -> minActivationFactorIsNull(it.getMinActivationFactor()) || minActivationFactorInProperRange(it.getMinActivationFactor()));
  }

  private boolean minActivationFactorIsNull(BigDecimal b) {
    return b == null;
  }

  private boolean minActivationFactorInProperRange(BigDecimal b) {
    return b.doubleValue() > 0.0 && b.doubleValue() <= 1.0;
  }

  @Override
  public String getReason() {
    return "Min activation factor must be between 0 and 1 (inclusive)";
  }
}
