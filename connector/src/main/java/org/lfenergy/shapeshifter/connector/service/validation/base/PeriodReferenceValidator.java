package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.AGRPortfolioQuery;
import org.lfenergy.shapeshifter.api.AGRPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioQuery;
import org.lfenergy.shapeshifter.api.DSOPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodReferenceValidator implements UftpBaseValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return AGRPortfolioQueryResponse.class.equals(clazz)
        || DSOPortfolioQueryResponse.class.equals(clazz)
        || FlexOffer.class.equals(clazz)
        || FlexOrder.class.equals(clazz);
  }

  @Override
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
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
    var request = support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(msg.getAGRPortfolioQueryMessageID(), AGRPortfolioQuery.class));
    if (request.isEmpty()) {
      return true; // validated in ReferencedRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.toLocalDate().isEqual(requestPeriod.toLocalDate());
  }

  private boolean validatePeriod(UftpMessage<PayloadMessageType> uftpMessage, DSOPortfolioQueryResponse msg) {
    var period = msg.getPeriod();
    var request = support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(msg.getDSOPortfolioQueryMessageID(), DSOPortfolioQuery.class));
    if (request.isEmpty()) {
      return true; // validated in ReferencedRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.toLocalDate().isEqual(requestPeriod.toLocalDate());
  }

  private boolean validatePeriod(UftpMessage<PayloadMessageType> uftpMessage, FlexOffer msg) {
    var period = msg.getPeriod();
    var requestId = Optional.ofNullable(msg.getFlexRequestMessageID());
    if (requestId.isEmpty()) {
      return true; // Unsolicited FlexOffer, thus no matching with FlexRequest period.
    }
    var request = support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(requestId.get(), FlexRequest.class));
    if (request.isEmpty()) {
      return true; // validated in ReferencedFlexRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.toLocalDate().isEqual(requestPeriod.toLocalDate());
  }

  private boolean validatePeriod(UftpMessage<PayloadMessageType> uftpMessage, FlexOrder msg) {
    var period = msg.getPeriod();
    var offer = support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(msg.getFlexOfferMessageID(), FlexOffer.class));
    if (offer.isEmpty()) {
      return true; // validated in ReferencedFlexOfferMessageIdValidation
    }
    var offerPeriod = offer.get().getPeriod();
    return period.toLocalDate().isEqual(offerPeriod.toLocalDate());
  }
}
