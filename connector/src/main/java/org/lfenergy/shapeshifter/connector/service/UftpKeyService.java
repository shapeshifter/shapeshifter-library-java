package org.lfenergy.shapeshifter.connector.service;

import java.util.Optional;
import org.lfenergy.shapeshifter.api.USEFRoleType;

public interface UftpKeyService {

  Optional<String> getParticipantPrivateKey(USEFRoleType role, String domain);

}
