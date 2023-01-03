package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestMappingErrorNotMatchingTypeSecondArgOnMethod {

  // Annotation message type must be the same as the second argument type
  @FlexRequestMapping
  public void onFlexRequest(UftpParticipant sender, FlexOrder message) {
  }
}
