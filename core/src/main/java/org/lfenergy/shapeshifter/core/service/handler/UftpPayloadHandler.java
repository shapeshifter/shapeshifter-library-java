package org.lfenergy.shapeshifter.core.service.handler;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.model.OutgoingUftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;

public interface UftpPayloadHandler {

  /**
   * @deprecated This method will be removed in the future. Use {@link #notifyNewIncomingMessage(IncomingUftpMessage)} instead.
   */
  @Deprecated(forRemoval = true, since = "2.3.0")
  default void notifyNewIncomingMessage(UftpParticipant from, PayloadMessageType message) {
    // Default implementation to allow switching to the new method without breaking later when it's removed.
    throw new UnsupportedOperationException("Deprecated for removal");
  }

  default void notifyNewIncomingMessage(IncomingUftpMessage<? extends PayloadMessageType> message) {
    notifyNewIncomingMessage(message.sender(), message.payloadMessage());
  }

  /**
   * @deprecated This method will be removed in the future. Use {@link #notifyNewOutgoingMessage(OutgoingUftpMessage)} instead.
   */
  @Deprecated(forRemoval = true, since = "2.3.0")
  default void notifyNewOutgoingMessage(UftpParticipant from, PayloadMessageType message) {
    // Default implementation to allow switching to the new method without breaking later when it's removed.
    throw new UnsupportedOperationException("Deprecated for removal");
  }

  default void notifyNewOutgoingMessage(OutgoingUftpMessage<? extends PayloadMessageType> message) {
    notifyNewOutgoingMessage(message.sender(), message.payloadMessage());
  }
}
