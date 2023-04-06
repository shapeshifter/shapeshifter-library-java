// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler.testmapping;

import org.lfenergy.shapeshifter.connector.service.handler.annotation.UftpIncomingHandler;
import org.springframework.web.bind.annotation.PostMapping;

@UftpIncomingHandler
public class UftpTestNoUftpMappings {

  // Must not be found if it does not have an UFTP Mapping annotation
  public void noAnnotation() {
  }

  // Must not be found if it does not have an UFTP Mapping annotation
  @PostMapping
  public void noneUftpAnnotation() {
  }
}
