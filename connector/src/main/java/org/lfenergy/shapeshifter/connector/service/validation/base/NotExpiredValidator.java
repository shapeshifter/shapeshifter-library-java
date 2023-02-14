package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class NotExpiredValidator implements UftpBaseValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexOffer.class.equals(clazz) || FlexOrder.class.equals(clazz);
  }

  @Override
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
    var payloadMessage = uftpMessage.payloadMessage();

    if (payloadMessage instanceof FlexOffer flexOffer) {
      return validateFlexRequestNotExpired(uftpMessage, flexOffer);
    }
    if (payloadMessage instanceof FlexOrder flexOrder) {
      return validateFlexOfferNotExpired(uftpMessage, flexOrder);
    }

    return true;
  }

  @Override
  public String getReason() {
    return "Reference message expired";
  }

  private boolean validateFlexRequestNotExpired(UftpMessage<PayloadMessageType> uftpMessage, FlexOffer msg) {
    var messageId = Optional.ofNullable(msg.getFlexRequestMessageID());
    if (messageId.isEmpty()) {
      return true;
    }

    var request = support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(messageId.get(), FlexRequest.class));
    return request.map(flexRequest -> validate(flexRequest.getExpirationDateTime())).orElse(true);
  }

  private boolean validateFlexOfferNotExpired(UftpMessage<PayloadMessageType> uftpMessage, FlexOrder msg) {
    var messageId = Optional.ofNullable(msg.getFlexOfferMessageID());
    if (messageId.isEmpty()) {
      return true;
    }

    var offer = support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(messageId.get(), FlexOffer.class));
    return offer.map(flexOffer -> validate(flexOffer.getExpirationDateTime())).orElse(true);
  }

  private boolean validate(OffsetDateTime expirationDateTime) {
    var now = OffsetDateTime.now();
    return now.isBefore(expirationDateTime);
  }
}
