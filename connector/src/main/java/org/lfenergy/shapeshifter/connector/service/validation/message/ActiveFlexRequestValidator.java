package org.lfenergy.shapeshifter.connector.service.validation.message;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Service;

/**
 * Validates that if a flex offer contains a flex request ID, the flex request ID refers to an active flex request (i.e. a flex request with an expiration date in the future)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveFlexRequestValidator implements UftpMessageValidator<FlexOffer> {

  private final UftpValidatorSupport uftpValidatorSupport;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOffer.class);
  }

  @Override
  public boolean valid(UftpParticipant sender, FlexOffer flexOffer) {
    // If there is no flex request ID, then the flex offer is unsolicited, which is allowed according to the spec
    if (isEmpty(flexOffer.getFlexRequestMessageID())) {
      return true;
    }
    var flexRequest = uftpValidatorSupport.getPreviousMessage(flexOffer.getFlexRequestMessageID(), FlexRequest.class);
    // If there is flex request ID, but there is no corresponding flex request, then return false
    if (flexRequest.isEmpty()) {
      return false;
    }
    // if the flex request is present, then it should be active (i.e. expiration date must be in the future)
    return flexRequest.map(this::expirationDateTimeIsInTheFuture).orElse(true);
  }

  private boolean expirationDateTimeIsInTheFuture(FlexRequest flexRequest) {
    return flexRequest.getExpirationDateTime().isAfter(OffsetDateTime.now());
  }

  @Override
  public String getReason() {
    return "Flex Offer does not contain a reference to an active Flex Request";
  }
}
