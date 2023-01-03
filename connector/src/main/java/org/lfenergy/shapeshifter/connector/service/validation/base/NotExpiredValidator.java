package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class NotExpiredValidator implements UftpBaseValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexOffer.class, m -> validateFlexRequestNotExired((FlexOffer) m),
          FlexOrder.class, m -> validateFlexOfferNotExired((FlexOrder) m)
      ));

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
    return "Reference message expired";
  }

  private boolean validateFlexRequestNotExired(FlexOffer msg) {
    var messageId = Optional.ofNullable(msg.getFlexRequestMessageID());
    if (messageId.isEmpty()) {
      return true;
    }

    var request = support.getPreviousMessage(messageId.get(), FlexRequest.class);
    return request.map(flexRequest -> validate(flexRequest.getExpirationDateTime())).orElse(true);
  }

  private boolean validateFlexOfferNotExired(FlexOrder msg) {
    var messageId = Optional.ofNullable(msg.getFlexOfferMessageID());
    if (messageId.isEmpty()) {
      return true;
    }

    var offer = support.getPreviousMessage(messageId.get(), FlexOffer.class);
    return offer.map(flexOffer -> validate(flexOffer.getExpirationDateTime())).orElse(true);
  }

  private boolean validate(OffsetDateTime expirationDateTime) {
    var now = OffsetDateTime.now();
    return now.isBefore(expirationDateTime);
  }
}
