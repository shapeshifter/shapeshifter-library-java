// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.testmapping;

import org.lfenergy.shapeshifter.connector.generated.handler.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestMappingErrorWrongSecondArgOnMethod {

  // The second argument type must be the same as annotation message type
  @FlexRequestMapping
  public void onFlexRequest(UftpParticipant sender, String message) {
  }
}
