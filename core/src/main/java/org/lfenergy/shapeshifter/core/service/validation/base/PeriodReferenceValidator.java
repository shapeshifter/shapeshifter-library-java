// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.AGRPortfolioQuery;
import org.lfenergy.shapeshifter.api.AGRPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioQuery;
import org.lfenergy.shapeshifter.api.DSOPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;


@RequiredArgsConstructor
public class PeriodReferenceValidator implements UftpValidator<PayloadMessageType> {

  private final UftpMessageSupport messageSupport;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return AGRPortfolioQueryResponse.class.equals(clazz)
        || DSOPortfolioQueryResponse.class.equals(clazz)
        || FlexOffer.class.equals(clazz)
        || FlexOrder.class.equals(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    var payloadMessage = uftpMessage.payloadMessage();

    if (payloadMessage instanceof AGRPortfolioQueryResponse agrPortfolioQueryResponse) {
      return validatePeriod(uftpMessage, agrPortfolioQueryResponse);
    }
    if (payloadMessage instanceof DSOPortfolioQueryResponse dsoPortfolioQueryResponse) {
      return validatePeriod(uftpMessage, dsoPortfolioQueryResponse);
    }
    if (payloadMessage instanceof FlexOffer flexOffer) {
      return validatePeriod(uftpMessage, flexOffer);
    }
    if (payloadMessage instanceof FlexOrder flexOrder) {
      return validatePeriod(uftpMessage, flexOrder);
    }

    return true;
  }

  @Override
  public String getReason() {
    return "Reference Period mismatch";
  }

  private boolean validatePeriod(UftpMessage<PayloadMessageType> uftpMessage, AGRPortfolioQueryResponse msg) {
    var period = msg.getPeriod();
    var request = messageSupport.getPreviousMessage(uftpMessage.payloadMessage().getConversationID(),
            uftpMessage.referenceToPreviousMessage(msg.getAGRPortfolioQueryMessageID(), msg.getConversationID(), AGRPortfolioQuery.class));
    if (request.isEmpty()) {
      return true; // validated in ReferencedRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.isEqual(requestPeriod);
  }

  private boolean validatePeriod(UftpMessage<PayloadMessageType> uftpMessage, DSOPortfolioQueryResponse msg) {
    var period = msg.getPeriod();
    var request = messageSupport.getPreviousMessage(uftpMessage.payloadMessage().getConversationID(),
            uftpMessage.referenceToPreviousMessage(msg.getDSOPortfolioQueryMessageID(), msg.getConversationID(), DSOPortfolioQuery.class));
    if (request.isEmpty()) {
      return true; // validated in ReferencedRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.isEqual(requestPeriod);
  }

  private boolean validatePeriod(UftpMessage<PayloadMessageType> uftpMessage, FlexOffer msg) {
    var period = msg.getPeriod();
    var flexRequestMessageID = Optional.ofNullable(msg.getFlexRequestMessageID());
    if (flexRequestMessageID.isEmpty()) {
      return true; // Unsolicited FlexOffer, thus no matching with FlexRequest period.
    }

    var request = messageSupport.getPreviousMessage(uftpMessage.payloadMessage().getConversationID(),
            uftpMessage.referenceToPreviousMessage(flexRequestMessageID.get(),
            msg.getConversationID(), FlexRequest.class));
    if (request.isEmpty()) {
      return true; // validated in ReferencedFlexRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.isEqual(requestPeriod);
  }

  private boolean validatePeriod(UftpMessage<PayloadMessageType> uftpMessage, FlexOrder msg) {
    var period = msg.getPeriod();
    var offer = messageSupport.getPreviousMessage(uftpMessage.payloadMessage().getConversationID(),
            uftpMessage.referenceToPreviousMessage(msg.getFlexOfferMessageID(),
            msg.getConversationID(), FlexOffer.class));
    if (offer.isEmpty()) {
      return true; // validated in ReferencedFlexOfferMessageIdValidation
    }
    var offerPeriod = offer.get().getPeriod();
    return period.isEqual(offerPeriod);
  }
}
