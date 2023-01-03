package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.util.Map;
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
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodReferenceValidator implements UftpBaseValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          AGRPortfolioQueryResponse.class, m -> validatePeriod((AGRPortfolioQueryResponse) m),
          DSOPortfolioQueryResponse.class, m -> validatePeriod((DSOPortfolioQueryResponse) m),
          FlexOffer.class, m -> validatePeriod((FlexOffer) m),
          FlexOrder.class, m -> validatePeriod((FlexOrder) m)
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.typeInMap(clazz);
  }

  @Override
  public boolean valid(UftpParticipant sender, PayloadMessageType payloadMessage) {
    return retriever.getProperty(payloadMessage);
  }

  @Override
  public String getReason() {
    return "Reference Period mismatch";
  }

  private boolean validatePeriod(AGRPortfolioQueryResponse msg) {
    var period = msg.getPeriod();
    var request = support.getPreviousMessage(msg.getAGRPortfolioQueryMessageID(), AGRPortfolioQuery.class);
    if (request.isEmpty()) {
      return true; // validated in ReferencedRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.toLocalDate().isEqual(requestPeriod.toLocalDate());
  }

  private boolean validatePeriod(DSOPortfolioQueryResponse msg) {
    var period = msg.getPeriod();
    var request = support.getPreviousMessage(msg.getDSOPortfolioQueryMessageID(), DSOPortfolioQuery.class);
    if (request.isEmpty()) {
      return true; // validated in ReferencedRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.toLocalDate().isEqual(requestPeriod.toLocalDate());
  }

  private boolean validatePeriod(FlexOffer msg) {
    var period = msg.getPeriod();
    var requestId = Optional.ofNullable(msg.getFlexRequestMessageID());
    if (requestId.isEmpty()) {
      return true; // Unsolicited FlexOffer, thus no matching with FlexRequest period.
    }
    var request = support.getPreviousMessage(requestId.get(), FlexRequest.class);
    if (request.isEmpty()) {
      return true; // validated in ReferencedFlexRequestMessageIdValidation
    }
    var requestPeriod = request.get().getPeriod();
    return period.toLocalDate().isEqual(requestPeriod.toLocalDate());
  }

  private boolean validatePeriod(FlexOrder msg) {
    var period = msg.getPeriod();
    var offer = support.getPreviousMessage(msg.getFlexOfferMessageID(), FlexOffer.class);
    if (offer.isEmpty()) {
      return true; // validated in ReferencedFlexOfferMessageIdValidation
    }
    var offerPeriod = offer.get().getPeriod();
    return period.toLocalDate().isEqual(offerPeriod.toLocalDate());
  }
}
