package org.lfenergy.shapeshifter.connector.model;

import org.lfenergy.shapeshifter.api.PayloadMessageType;

public final class UftpMessageFixture {

  private UftpMessageFixture() {
    // Utility class
  }

  public static <T extends PayloadMessageType> UftpMessage<T> createIncoming(UftpParticipant sender, T payloadMessage) {
    return new UftpMessage<>(sender, UftpMessageDirection.INCOMING, payloadMessage);
  }

  public static <T extends PayloadMessageType> UftpMessage<T> createOutgoing(UftpParticipant sender, T payloadMessage) {
    return new UftpMessage<>(sender, UftpMessageDirection.OUTGOING, payloadMessage);
  }

}
