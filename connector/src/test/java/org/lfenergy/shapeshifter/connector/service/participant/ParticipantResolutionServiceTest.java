package org.lfenergy.shapeshifter.connector.service.participant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.api.model.UftpParticipantInformation;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.UftpParticipantService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParticipantResolutionServiceTest {

  private static final USEFRoleType ROLE = USEFRoleType.AGR;
  private static final String DOMAIN_NAME = "DOMAIN_NAME";
  private static final String ENDPOINT = "ENDPOINT";
  private static final String PUBLIC_KEY = "PUBLIC_KEY";

  @Mock
  private UftpParticipantService uftpParticipantService;

  @InjectMocks
  private ParticipantResolutionService testSubject;

  @Mock
  private UftpParticipant recipient;
  @Mock
  private UftpParticipantInformation information;

  @Test
  void getEndPointUrl_found() {
    given(recipient.role()).willReturn(ROLE);
    given(recipient.domain()).willReturn(DOMAIN_NAME);
    given(uftpParticipantService.getParticipantInformation(ROLE, DOMAIN_NAME)).willReturn(Optional.of(information));
    given(information.endpoint()).willReturn(ENDPOINT);

    assertThat(testSubject.getEndPointUrl(recipient)).isEqualTo(ENDPOINT);
  }

  @Test
  void getEndPointUrl_notFound() {
    given(recipient.role()).willReturn(ROLE);
    given(recipient.domain()).willReturn(DOMAIN_NAME);
    given(uftpParticipantService.getParticipantInformation(ROLE, DOMAIN_NAME)).willReturn(Optional.empty());

    var thrown = assertThrows(UftpConnectorException.class, () ->
        testSubject.getEndPointUrl(recipient));

    assertThat(thrown.getMessage()).isEqualTo("No participant found for DOMAIN_NAME in AGR");
  }

  @Test
  void getPublicKey() {
    given(uftpParticipantService.getParticipantInformation(ROLE, DOMAIN_NAME)).willReturn(Optional.of(information));
    given(information.publicKey()).willReturn(PUBLIC_KEY);

    assertThat(testSubject.getPublicKey(ROLE, DOMAIN_NAME)).isEqualTo(PUBLIC_KEY);
  }

  @Test
  void getPublicKey_notFound() {
    given(uftpParticipantService.getParticipantInformation(ROLE, DOMAIN_NAME)).willReturn(Optional.empty());

    var thrown = assertThrows(UftpConnectorException.class, () ->
        testSubject.getPublicKey(ROLE, DOMAIN_NAME));

    assertThat(thrown.getMessage()).isEqualTo("No participant found for DOMAIN_NAME in AGR");
  }
}