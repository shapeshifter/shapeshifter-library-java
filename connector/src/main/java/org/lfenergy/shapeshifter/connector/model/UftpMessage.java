// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.model;

import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessageResponse;

public record UftpMessage<T extends PayloadMessageType>(
    UftpParticipant sender,
    UftpMessageDirection direction,
    T payloadMessage
) {

  public static <T extends PayloadMessageType> UftpMessage<T> createIncoming(UftpParticipant sender, T payloadMessage) {
    return new UftpMessage<>(sender, UftpMessageDirection.INCOMING, payloadMessage);
  }

  public static <T extends PayloadMessageType> UftpMessage<T> createOutgoing(UftpParticipant sender, T payloadMessage) {
    return new UftpMessage<>(sender, UftpMessageDirection.OUTGOING, payloadMessage);
  }

  /**
   * Constructs a reference to a previous message (e.g. FlexRequest) referenced by this message (e.g. FlexOffer), by flipping the direction and sender and recipient domains.
   *
   * @param referencedMessageID The MessageID that is being referenced (e.g. the MessageID of the FlexRequest)
   * @param referencedType The type of message that is being referenced (e.g. FlexRequest)
   * @param <U> The type of message that is being referenced (e.g. FlexRequest).
   * @return Reference to the referenced message.
   */
  public <U extends PayloadMessageType> UftpMessageReference<U> referenceToPreviousMessage(String referencedMessageID, Class<U> referencedType) {
    return new UftpMessageReference<>(referencedMessageID,
                                      // Having the correct direction is crucial for distinguishing between sender and recipient domain
                                      direction.inverse(),
                                      // Flip domains as the referencing message is sent by the recipient of the referenced message
                                      payloadMessage.getRecipientDomain(),
                                      payloadMessage.getSenderDomain(),
                                      referencedType);
  }

  public static boolean isResponse(PayloadMessageType message) {
    return message instanceof PayloadMessageResponseType || message instanceof TestMessageResponse;
  }
}
