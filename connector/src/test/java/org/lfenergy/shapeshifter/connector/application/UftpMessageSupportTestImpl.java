// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.application;

import java.util.Optional;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessageReference;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageSupport;
import org.springframework.stereotype.Component;

@Component
public class UftpMessageSupportTestImpl implements UftpMessageSupport {

  @Override
  public Optional<PayloadMessageType> getPreviousMessage(String messageID, String recipientDomain) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T extends PayloadMessageType> Optional<T> getPreviousMessage(UftpMessageReference<T> reference) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isValidOrderReference(String orderReference, String recipientDomain) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean existsFlexRevocation(String flexOfferMessageId, String recipientDomain) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
