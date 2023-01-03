package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestMappingErrorWrongSecondArgOnMethod {

  // The second argument type must be the same as annotation message type
  @FlexRequestMapping
  public void onFlexRequest(UftpParticipant sender, String message) {
  }
}
