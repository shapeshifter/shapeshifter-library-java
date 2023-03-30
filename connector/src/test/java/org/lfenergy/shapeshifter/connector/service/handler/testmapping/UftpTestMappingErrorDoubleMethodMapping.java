// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.testmapping;

import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.generated.handler.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpIncomingHandler;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpMapping;

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
