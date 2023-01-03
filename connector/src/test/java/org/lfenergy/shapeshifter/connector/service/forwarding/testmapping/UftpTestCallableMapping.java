package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexOfferMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestCallableMapping {

  public record Info(UftpParticipant sender, PayloadMessageType message) {

  }

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
}
