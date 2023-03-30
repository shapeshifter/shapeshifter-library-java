// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.testmapping;

import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.connector.generated.handler.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestMappingErrorNotMatchingTypeSecondArgOnMethod {

  // Annotation message type must be the same as the second argument type
  @FlexRequestMapping
  public void onFlexRequest(UftpParticipant sender, FlexOrder message) {
  }
}
