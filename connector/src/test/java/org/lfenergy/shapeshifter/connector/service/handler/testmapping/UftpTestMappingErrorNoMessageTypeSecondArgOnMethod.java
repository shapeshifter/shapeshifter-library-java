// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.testmapping;

import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpIncomingHandler;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpMapping;

@UftpIncomingHandler
public class UftpTestMappingErrorNoMessageTypeSecondArgOnMethod {

  // Annotation type must be provided.
  @UftpMapping
  public void onFlexRequest(UftpParticipant sender, FlexMessageType message) {
  }
}
