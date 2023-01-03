package org.lfenergy.shapeshifter.connector.application;

import java.util.Optional;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.service.UftpKeyService;
import org.springframework.stereotype.Component;

@Component
public class UftpKeyServiceTestImpl implements UftpKeyService {

  @Override
  public Optional<String> getParticipantPrivateKey(USEFRoleType role, String domain) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
