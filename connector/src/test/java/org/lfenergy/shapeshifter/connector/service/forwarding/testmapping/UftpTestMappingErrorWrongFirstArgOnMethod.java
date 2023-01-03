package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestMappingErrorWrongFirstArgOnMethod {

  // First argument must be of type SenderInformation
  @FlexRequestMapping
  public void onFlexRequest(String sender, FlexRequest message) {
  }
}
