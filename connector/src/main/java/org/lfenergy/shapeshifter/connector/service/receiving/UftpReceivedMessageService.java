package org.lfenergy.shapeshifter.connector.service.receiving;

import static org.lfenergy.shapeshifter.connector.model.UftpRoleInformation.getRecipientRoleBySenderRole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.config.UftpConnectorConfig;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.UftpPayloadForwarder;
import org.lfenergy.shapeshifter.connector.service.receiving.response.UftpValidationResponseCreator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.connector.service.validation.model.ValidationResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UftpReceivedMessageService {

  private final UftpConnectorConfig config;
  private final UftpValidationService validator;
  private final UftpPayloadForwarder forwarder;

  public ValidationResult process(UftpParticipant from, PayloadMessageType payloadMessage) {
    var validationResult = validateMessage(from, payloadMessage);

    if (payloadMessage instanceof PayloadMessageResponseType response) {
      if (!validationResult.valid()) {
        log.warn("Received invalid {} response {}. {}.", response.getResult().value(), response.getMessageID(), validationResult.rejectionReason());
      }
    } else {
      var response = UftpValidationResponseCreator.getResponseForMessage(payloadMessage, validationResult);
      var originalRecipient = new UftpParticipant(payloadMessage.getRecipientDomain(), getRecipientRoleBySenderRole(from.role()));

      forwarder.notifyNewOutgoingMessage(originalRecipient, response);
    }

    return validationResult;
  }

  private ValidationResult validateMessage(UftpParticipant from, PayloadMessageType payloadMessage) {
    if (shouldPerformValidations()) {
      return validator.validate(UftpMessage.createIncoming(from, payloadMessage));
    }
    return ValidationResult.ok();
  }

  private boolean shouldPerformValidations() {
    return config.receiving().validation().enabled();
  }

}
