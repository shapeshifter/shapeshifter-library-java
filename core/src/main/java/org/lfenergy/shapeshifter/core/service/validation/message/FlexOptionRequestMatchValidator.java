// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;

/**
 * Validates that at least one of the ISPs with a 'requested'”' disposition in the referred FlexRequest, is mentioned in the FlexOffer.
 */


@RequiredArgsConstructor
public class FlexOptionRequestMatchValidator implements UftpValidator<FlexOffer> {

  private final UftpMessageSupport messageSupport;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOffer.class);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean isValid(UftpMessage<FlexOffer> uftpMessage) {
    var flexOffer = uftpMessage.payloadMessage();

    if (flexOffer.getFlexRequestMessageID() == null) {
      return true;
    }

    var flexRequest = messageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexOffer.getFlexRequestMessageID(), FlexRequest.class));
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
