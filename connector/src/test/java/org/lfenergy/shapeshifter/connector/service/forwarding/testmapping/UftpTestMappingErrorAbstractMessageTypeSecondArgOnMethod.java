package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpMapping;

@UftpIncomingHandler
public class UftpTestMappingErrorAbstractMessageTypeSecondArgOnMethod {

  // Annotation type must be a none-abstract class.
  @UftpMapping(type = FlexMessageType.class)
  public void onFlexRequest(UftpParticipant sender, FlexMessageType message) {
  }
}
