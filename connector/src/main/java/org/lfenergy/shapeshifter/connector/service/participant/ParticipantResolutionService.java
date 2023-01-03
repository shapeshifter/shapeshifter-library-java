package org.lfenergy.shapeshifter.connector.service.participant;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.api.model.UftpParticipantInformation;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.UftpParticipantService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantResolutionService {

  private final UftpParticipantService uftpParticipantService;

  public String getEndPointUrl(UftpParticipant recipient) {
    return getDomain(recipient.role(), recipient.domain()).endpoint();
  }

  public String getPublicKey(USEFRoleType senderRole, String senderDomain) {
    return getDomain(senderRole, senderDomain).publicKey();
  }

  private UftpParticipantInformation getDomain(USEFRoleType role, String domain) {
    return uftpParticipantService.getParticipantInformation(role, domain).orElseThrow(
        () -> new UftpConnectorException("No participant found for " + domain + " in " + role));
  }
}
