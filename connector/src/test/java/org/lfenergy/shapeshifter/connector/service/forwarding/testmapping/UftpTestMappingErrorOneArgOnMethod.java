package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestMappingErrorOneArgOnMethod {

  // Must have two arguments
  @FlexRequestMapping
  public void onFlexRequest(UftpParticipant sender) {
  }
}
