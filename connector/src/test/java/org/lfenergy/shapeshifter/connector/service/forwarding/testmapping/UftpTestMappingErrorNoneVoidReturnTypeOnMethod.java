package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestMappingErrorNoneVoidReturnTypeOnMethod {

  // Return type must be void
  @FlexRequestMapping
  public String onFlexRequest(UftpParticipant sender, FlexRequest message) {
    return "aap";
  }
}
