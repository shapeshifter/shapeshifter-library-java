package org.lfenergy.shapeshifter.connector.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShippingDetailsTest {

  public static final String RECIPIENT_DOMAIN = "RECIPIENT_DOMAIN";
  public static final String PRIVATE_KEY = "PRIVATE_KEY";

  @Mock
  private UftpParticipant sender;
  @Mock
  private UftpParticipant recipient;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(sender, recipient);
  }

  @Test
  void construction() {
    var testSubject = new ShippingDetails(sender, PRIVATE_KEY, recipient);

    assertThat(testSubject.sender()).isSameAs(sender);
    assertThat(testSubject.senderPrivateKey()).isEqualTo(PRIVATE_KEY);
    assertThat(testSubject.recipient()).isSameAs(recipient);
  }
}