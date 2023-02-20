package org.lfenergy.shapeshifter.connector.service.validation.message;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

/**
 * Validates that at least one of the ISPs with a 'requested'”' disposition in the referred FlexRequest, is mentioned in the FlexOffer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlexOptionRequestMatchValidator implements UftpMessageValidator<FlexOffer> {

  private final UftpValidatorSupport uftpValidatorSupport;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOffer.class);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean valid(UftpMessage<FlexOffer> uftpMessage) {
    var flexOffer = uftpMessage.payloadMessage();

    if (flexOffer.getFlexRequestMessageID() == null) {
      return true;
    }

    var flexRequest = uftpValidatorSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexOffer.getFlexRequestMessageID(), FlexRequest.class));
    // if there is no flex request, then this is an unsolicited flex offer, which is perfectly fine
    if (flexRequest.isEmpty()) {
      return true;
    }
    var allIspsInFlexOffers = getAllOfferOptionISPs(flexOffer);
    return ispsWithDispositionRequest(flexRequest.get()).stream().anyMatch(it -> ispAppearsInFlexOffer(it, allIspsInFlexOffers));
  }

  private boolean ispAppearsInFlexOffer(FlexRequestISPType ispInRequest, List<FlexOfferOptionISPType> ispsInOffer) {
    return ispsInOffer.stream().anyMatch(it -> match(ispInRequest, it));
  }

  private List<FlexRequestISPType> ispsWithDispositionRequest(FlexRequest flexRequest) {
    return flexRequest.getISPS().stream().filter(it -> it.getDisposition().equals(AvailableRequestedType.REQUESTED)).toList();
  }

  private List<FlexOfferOptionISPType> getAllOfferOptionISPs(FlexOffer flexOffer) {
    return flexOffer.getOfferOptions().stream().map(FlexOfferOptionType::getISPS).flatMap(List::stream).toList();
  }

  private boolean match(FlexRequestISPType ispInRequest, FlexOfferOptionISPType ispInOffer) {
    return ispInRequest.getStart().longValue() == ispInOffer.getStart().longValue() &&
        ispInRequest.getDuration() == ispInOffer.getDuration();
  }

  @Override
  public String getReason() {
    return "None of the ISPs with a 'requested' disposition in the referred FlexRequest, is mentioned in the FlexOffer";
  }
}
