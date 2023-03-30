// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.receiving;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.DUPLICATE_MESSAGE;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.NEW_MESSAGE;
import static org.lfenergy.shapeshifter.connector.service.receiving.DuplicateMessageDetection.DuplicateMessageResult.REUSED_ID_DIFFERENT_CONTENT;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DuplicateMessageDetectionTest {

  private static final String MESSAGE_ID = "MESSAGE_ID";
  private static final String RECIPIENT_DOMAIN = "test.nl";
  private static final String CONTENT_NEW_MESSAGE = "CONTENT_NEW_MESSAGE";
  private static final String CONTENT_PREVIOUS_MESSAGE = "CONTENT_PREVIOUS_MESSAGE";

  @Mock
  private UftpMessageSupport support;
  @Mock
  private XmlSerializer serializer;

  @InjectMocks
  private DuplicateMessageDetection testSubject;

  @Mock
  private FlexRequest newMessage;
  @Mock
  private FlexRequest previousFlexRequest;
  @Mock
  private FlexOffer previousFlexOffer;

  @BeforeEach
  void setup() {
    given(newMessage.getMessageID()).willReturn(MESSAGE_ID);
    given(newMessage.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);

  }

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        serializer,
        newMessage,
        previousFlexRequest,
        previousFlexOffer
    );
  }

  @Test
  void isDuplicate_resultNewMessage_whenUnknownMessageId() {
    given(support.getPreviousMessage(MESSAGE_ID, RECIPIENT_DOMAIN)).willReturn(Optional.empty());

    assertThat(testSubject.isDuplicate(newMessage)).isEqualTo(NEW_MESSAGE);
  }

  @Test
  void isDuplicate_resultReusedIdDiffContent_whenKownMessageWithOtherType() {
    given(support.getPreviousMessage(MESSAGE_ID, RECIPIENT_DOMAIN)).willReturn(Optional.of(previousFlexOffer));

    assertThat(testSubject.isDuplicate(newMessage)).isEqualTo(REUSED_ID_DIFFERENT_CONTENT);
  }

  @Test
  void isDuplicate_resultReusedIdDiffContent_whenKownMessageWithSameTypeDiffContent() {
    given(support.getPreviousMessage(MESSAGE_ID, RECIPIENT_DOMAIN)).willReturn(Optional.of(previousFlexRequest));
    given(serializer.toXml(newMessage)).willReturn(CONTENT_NEW_MESSAGE);
    given(serializer.toXml(previousFlexRequest)).willReturn(CONTENT_PREVIOUS_MESSAGE);

    assertThat(testSubject.isDuplicate(newMessage)).isEqualTo(REUSED_ID_DIFFERENT_CONTENT);
  }

  @Test
  void isDuplicate_resultDuplicateMessage_whenKownMessageWithSameTypeSameContent() {
    given(support.getPreviousMessage(MESSAGE_ID, RECIPIENT_DOMAIN)).willReturn(Optional.of(previousFlexRequest));
    given(serializer.toXml(newMessage)).willReturn(CONTENT_PREVIOUS_MESSAGE);
    given(serializer.toXml(previousFlexRequest)).willReturn(CONTENT_PREVIOUS_MESSAGE);

    assertThat(testSubject.isDuplicate(newMessage)).isEqualTo(DUPLICATE_MESSAGE);
  }
}
