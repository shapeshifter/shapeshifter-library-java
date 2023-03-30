// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.receiving.response;

import java.time.OffsetDateTime;
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

  public static PayloadMessageType getResponseForMessage(PayloadMessageType request, ValidationResult result) {
    var responseType = UftpRequestResponseMapping.getResponseTypeFor(request);
    var response = UftpMessageFactory.instantiate(responseType);

    response.setTimeStamp(OffsetDateTime.now());
    response.setSenderDomain(request.getRecipientDomain());
    response.setRecipientDomain(request.getSenderDomain());
    response.setMessageID(UUID.randomUUID().toString());
    response.setConversationID(request.getConversationID());
    response.setVersion(request.getVersion());

    if (response instanceof PayloadMessageResponseType resultResponse) {
      resultResponse.setResult(result.valid() ? AcceptedRejectedType.ACCEPTED : AcceptedRejectedType.REJECTED);
      resultResponse.setRejectionReason(result.valid() ? null : result.rejectionReason());
      UftpRequestResponseMapping.setReferencedRequestMessageId(request, resultResponse);
    }

    return response;
  }

}
