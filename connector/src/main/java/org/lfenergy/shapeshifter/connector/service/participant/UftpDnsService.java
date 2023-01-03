package org.lfenergy.shapeshifter.connector.service.participant;

import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.springframework.stereotype.Service;

@Service
public class UftpDnsService {

  public String getPublicKey(String senderDomain, USEFRoleType senderRole) {
    throw new UftpConnectorException("Failed to retrieve public key for " + senderDomain + "/" + senderRole + " from DNS.", 419);
  }

  public String getEndPointUrl(UftpParticipant recipient) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
