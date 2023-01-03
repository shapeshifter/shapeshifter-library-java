package org.lfenergy.shapeshifter.connector.service.participant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertException;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpDnsServiceTest {

  private static final String SENDER_DOMAIN = "SENDER_DOMAIN";

  @InjectMocks
  private UftpDnsService testSubject;

  @Mock
  private UftpParticipant recipient;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        recipient
    );
  }

  @Test
  void getPublicKey() {
    UftpConnectorException thrown = assertThrows(UftpConnectorException.class, () ->
        testSubject.getPublicKey(SENDER_DOMAIN, USEFRoleType.DSO));

    assertException(thrown, "Failed to retrieve public key for SENDER_DOMAIN/DSO from DNS.", 419);
  }

  @Test
  void getEndPointUrl() {
    assertThrows(UnsupportedOperationException.class, () -> testSubject.getEndPointUrl(recipient));
  }
}