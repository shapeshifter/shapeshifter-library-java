package org.lfenergy.shapeshifter.connector.service;

import java.util.Optional;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.api.model.UftpParticipantInformation;

public interface UftpParticipantService {

  Optional<UftpParticipantInformation> getParticipantInformation(USEFRoleType role, String domain);

}
