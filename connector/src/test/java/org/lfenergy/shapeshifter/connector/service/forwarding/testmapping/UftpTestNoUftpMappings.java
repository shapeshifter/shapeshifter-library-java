package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;
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
