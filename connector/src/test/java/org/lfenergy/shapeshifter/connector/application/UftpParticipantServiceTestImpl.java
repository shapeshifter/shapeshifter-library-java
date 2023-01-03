package org.lfenergy.shapeshifter.connector.application;

import java.util.Optional;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.api.model.UftpParticipantInformation;
import org.lfenergy.shapeshifter.connector.service.UftpParticipantService;
import org.springframework.stereotype.Component;

@Component
public class UftpParticipantServiceTestImpl implements UftpParticipantService {

  @Override
  public Optional<UftpParticipantInformation> getParticipantInformation(USEFRoleType role, String domain) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
