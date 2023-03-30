// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.testmapping;

import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.generated.handler.annotation.FlexOfferMapping;
import org.lfenergy.shapeshifter.connector.generated.handler.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestCallableMapping {

  public Info calledFlexOffer = null;
  public Info calledFlexRequest = null;

  @FlexOfferMapping
  public void onFlexOffer(UftpParticipant sender, FlexOffer message) {
    calledFlexOffer = new Info(sender, message);
    throw new UftpConnectorException("thrown from onFlexOffer");
  }

  @FlexRequestMapping
  public void onFlexRequest(UftpParticipant sender, FlexRequest message) {
    calledFlexRequest = new Info(sender, message);
  }

  public record Info(UftpParticipant sender, PayloadMessageType message) {

  }
}
