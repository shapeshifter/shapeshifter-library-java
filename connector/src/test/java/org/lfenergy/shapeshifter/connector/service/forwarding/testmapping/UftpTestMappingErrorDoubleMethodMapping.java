package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpMapping;

@UftpIncomingHandler
public class UftpTestMappingErrorDoubleMethodMapping {

  // Not allowed to have two mapping methods for the same UFTP message type
  @FlexRequestMapping
  public void onFlexRequestDirect(UftpParticipant sender, FlexRequest message) {

  }

  // Not allowed to have two mapping methods for the same UFTP message type
  @UftpMapping(type = FlexRequest.class)
  public void onFlexRequestBase(UftpParticipant sender, FlexRequest message) {

  }
}
