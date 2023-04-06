// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.testmapping;

import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpIncomingHandler;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpMapping;

@UftpIncomingHandler
public class UftpTestMappingErrorAbstractMessageTypeSecondArgOnMethod {

  // Annotation type must be a none-abstract class.
  @UftpMapping(type = FlexMessageType.class)
  public void onFlexRequest(UftpParticipant sender, FlexMessageType message) {
  }
}
