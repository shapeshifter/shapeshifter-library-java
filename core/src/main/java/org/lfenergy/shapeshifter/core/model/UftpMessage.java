// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.model;

import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessageResponse;

public abstract sealed class UftpMessage<T extends PayloadMessageType> permits IncomingUftpMessage, OutgoingUftpMessage {

  private final UftpParticipant sender;
  private final T payloadMessage;

  protected UftpMessage(UftpParticipant sender, T payloadMessage) {
    this.sender = sender;
    this.payloadMessage = payloadMessage;
  }

  public static <T extends PayloadMessageType> IncomingUftpMessage<T> createIncoming(UftpParticipant sender, T payloadMessage, String signedMessageXml, String payloadMessageXml) {
    return IncomingUftpMessage.create(sender, payloadMessage, signedMessageXml, payloadMessageXml);
  }

  public static <T extends PayloadMessageType> OutgoingUftpMessage<T> createOutgoing(UftpParticipant sender, T payloadMessage) {
    return OutgoingUftpMessage.create(sender, payloadMessage);
  }

  /**
   * Constructs a reference to a previous message (e.g. FlexRequest) referenced by this message (e.g. FlexOffer), by flipping the direction and sender and recipient domains.
   *
   * @param referencedMessageID The MessageID that is being referenced (e.g. the MessageID of the FlexRequest)
   * @param referencedType The type of message that is being referenced (e.g. FlexRequest)
   * @param <U> The type of message that is being referenced (e.g. FlexRequest).
   * @return Reference to the referenced message.
   */
  public <U extends PayloadMessageType> UftpMessageReference<U> referenceToPreviousMessage(String referencedMessageID,
                                                                                           String conversationID,
                                                                                           Class<U> referencedType) {
    return new UftpMessageReference<>(referencedMessageID,
                                      conversationID,
                                      // Having the correct direction is crucial for distinguishing between sender and recipient domain
                                      direction().inverse(),
                                      // Flip domains as the referencing message is sent by the recipient of the referenced message
                                      payloadMessage.getRecipientDomain(),
                                      payloadMessage.getSenderDomain(),
                                      referencedType);
  }

  public static boolean isResponse(PayloadMessageType message) {
    return message instanceof PayloadMessageResponseType || message instanceof TestMessageResponse;
  }

  public UftpParticipant sender() {
    return sender;
  }

  public T payloadMessage() {
    return payloadMessage;
  }

  public abstract  UftpMessageDirection direction();
}
