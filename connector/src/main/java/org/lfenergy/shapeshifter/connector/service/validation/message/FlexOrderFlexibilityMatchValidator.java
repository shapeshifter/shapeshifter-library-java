// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.message;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderISPType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

/**
 * Validates that at the ordered power matches the offered power
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlexOrderFlexibilityMatchValidator implements UftpValidator<FlexOrder> {

  private final UftpMessageSupport uftpMessageSupport;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexOrder.class);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean isValid(UftpMessage<FlexOrder> uftpMessage) {
    var flexOrder = uftpMessage.payloadMessage();

    var flexOffer = uftpMessageSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexOrder.getFlexOfferMessageID(), FlexOffer.class));
    if (flexOffer.isEmpty()) {
      return false;
    }

    if (flexOrder.getOptionReference() != null && !flexOrder.getOptionReference().isBlank()) {
      return validForOptionReference(flexOrder, flexOffer.get());
    }
    return validWithoutOptionReference(flexOrder, flexOffer.get());
  }

  private boolean powerMatches(FlexOrderISPType ispInOrder, List<FlexOfferOptionISPType> ispsInOffer) {
    return ispsInOffer.stream().anyMatch(it -> powerMatches(ispInOrder, it));
  }

  private boolean powerMatches(FlexOrderISPType ispInOrder, FlexOfferOptionISPType ispInOffer) {
    return ispInOrder.getPower().equals(ispInOffer.getPower()) &&
        ispInOrder.getStart().equals(ispInOffer.getStart()) &&
        ispInOrder.getDuration() == ispInOffer.getDuration();
  }

  private boolean validForOptionReference(FlexOrder flexOrder, FlexOffer flexOffer) {
    var flexOfferOptionsFiltered = flexOffer.getOfferOptions().stream().filter(
        it -> it.getOptionReference() != null && !it.getOptionReference().isBlank() && it.getOptionReference().equals(flexOrder.getOptionReference())).toList();
    if (flexOfferOptionsFiltered.isEmpty()) {
      // Option reference does not refer to an existing option in the flex offer message
      return false;
    }
    if (flexOfferOptionsFiltered.size() > 1) {
      // Option reference refers to more than one flex offer option in the flex offer message
      return false;
    }
    return flexOrder.getISPS().stream().allMatch(it -> powerMatches(it, flexOfferOptionsFiltered.get(0).getISPS()));
  }

  private boolean validWithoutOptionReference(FlexOrder flexOrder, FlexOffer flexOffer) {
    if (flexOffer.getOfferOptions().size() == 1) {
      return flexOrder.getISPS().stream().allMatch(it -> powerMatches(it, flexOffer.getOfferOptions().get(0).getISPS()));
    }
    return false;
  }

  @Override
  public String getReason() {
    return "Ordered flexibility does not match the offered flexibility";
  }
}