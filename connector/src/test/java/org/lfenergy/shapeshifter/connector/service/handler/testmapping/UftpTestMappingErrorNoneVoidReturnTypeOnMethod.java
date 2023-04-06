// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.testmapping;

import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.generated.handler.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestMappingErrorNoneVoidReturnTypeOnMethod {

  // Return type must be void
  @FlexRequestMapping
  public String onFlexRequest(UftpParticipant sender, FlexRequest message) {
    return "aap";
  }
}
