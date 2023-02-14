package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SenderMatchesEnvelopeValidatorTest {

  private static final String SENDER_DOMAIN = "SENDER_DOMAIN";
  private static final String DIFFERENT = "DIFFERENT";

  @InjectMocks
  private SenderMatchesEnvelopeValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private PayloadMessageType payloadMessage;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        sender,
        payloadMessage
    );
  }

  @Test
  void appliesTo_allTypes() {
    assertThat(testSubject.appliesTo(PayloadMessageType.class)).isTrue();
  }

  @Test
  void valid_true_whenSame() {
    given(sender.domain()).willReturn(SENDER_DOMAIN);
    given(payloadMessage.getSenderDomain()).willReturn(SENDER_DOMAIN);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @Test
  void valid_false_whenDifferent() {
    given(sender.domain()).willReturn(SENDER_DOMAIN);
    given(payloadMessage.getSenderDomain()).willReturn(DIFFERENT);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Invalid Sender (not matching envelope)");
  }
}