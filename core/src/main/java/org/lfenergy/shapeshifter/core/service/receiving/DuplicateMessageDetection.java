// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.receiving;


import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;

@RequiredArgsConstructor
public class DuplicateMessageDetection {

  public enum DuplicateMessageResult {
    NEW_MESSAGE,
    REUSED_ID_DIFFERENT_CONTENT,
    DUPLICATE_MESSAGE
  }

  private final UftpMessageSupport support;
  private final XmlSerializer serializer;

  public DuplicateMessageResult isDuplicate(PayloadMessageType newMessage) {
    var previousMessage = support.findDuplicateMessage(newMessage.getMessageID(), newMessage.getSenderDomain(), newMessage.getRecipientDomain());
    if (previousMessage.isEmpty()) {
      return DuplicateMessageResult.NEW_MESSAGE;
    }
    if (isSameMessage(previousMessage.get(), newMessage)) {
      return DuplicateMessageResult.DUPLICATE_MESSAGE;
    }
    return DuplicateMessageResult.REUSED_ID_DIFFERENT_CONTENT;
  }

  private boolean isSameMessage(PayloadMessageType previousMessage, PayloadMessageType newMessage) {
    return isSameType(previousMessage, newMessage) && isSameContent(previousMessage, newMessage);
  }

  private boolean isSameType(PayloadMessageType previousMessage, PayloadMessageType newMessage) {
    return previousMessage.getClass().equals(newMessage.getClass());
  }

  private boolean isSameContent(PayloadMessageType previousMessage, PayloadMessageType newMessage) {
    return serializer.toXml(previousMessage).equals(serializer.toXml(newMessage));
  }
}
