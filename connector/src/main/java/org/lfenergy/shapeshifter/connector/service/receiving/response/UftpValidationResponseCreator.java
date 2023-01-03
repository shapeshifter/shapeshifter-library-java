package org.lfenergy.shapeshifter.connector.service.receiving.response;

import java.util.UUID;
import org.lfenergy.shapeshifter.api.AcceptedRejectedType;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpRequestResponseMapping;
import org.lfenergy.shapeshifter.connector.service.validation.model.ValidationResult;

public class UftpValidationResponseCreator {

  private UftpValidationResponseCreator() {
    // Static creator class
  }

  public static PayloadMessageResponseType getResponseForMessage(PayloadMessageType request, ValidationResult result) {
    var responseType = UftpRequestResponseMapping.getResponseTypeFor(request);
    var response = UftpResponseMessageFactory.instantiate(responseType);

    response.setSenderDomain(request.getRecipientDomain());
    response.setRecipientDomain(request.getSenderDomain());
    response.setMessageID(UUID.randomUUID().toString());
    response.setConversationID(request.getConversationID());
    response.setResult(result.valid() ? AcceptedRejectedType.ACCEPTED : AcceptedRejectedType.REJECTED);
    response.setRejectionReason(result.valid() ? null : result.rejectionReason());
    response.setVersion(request.getVersion());

    UftpRequestResponseMapping.setReferencedRequestMessageId(request, response);

    return response;
  }

}
