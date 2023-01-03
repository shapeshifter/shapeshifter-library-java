package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.DUPLICATE_MESSAGE;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.NEW_MESSAGE;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.REUSED_ID_DIFFERENT_CONTENT;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DuplicateIdentifierValidatorTest {

  @Mock
  private DuplicateMessageDetection duplicateDetection;

  @InjectMocks
  private DuplicateIdentifierValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private PayloadMessageType payloadMessage;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        duplicateDetection,
        sender,
        payloadMessage
    );
  }

  @Test
  void appliesTo_allTypes() {
    assertThat(testSubject.appliesTo(PayloadMessageType.class)).isTrue();
  }

  @Test
  void valid_true_whenNewMessage() {
    given(duplicateDetection.isDuplicate(payloadMessage)).willReturn(NEW_MESSAGE);

    assertThat(testSubject.valid(sender, payloadMessage)).isTrue();
  }

  @Test
  void valid_true_whenDuplicateMessage() {
    given(duplicateDetection.isDuplicate(payloadMessage)).willReturn(DUPLICATE_MESSAGE);

    assertThat(testSubject.valid(sender, payloadMessage)).isTrue();
  }

  @Test
  void valid_false_whenReusedIdDiffContent() {
    given(duplicateDetection.isDuplicate(payloadMessage)).willReturn(REUSED_ID_DIFFERENT_CONTENT);

    assertThat(testSubject.valid(sender, payloadMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Duplicate Identifier");
  }
}