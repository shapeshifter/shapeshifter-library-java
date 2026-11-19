package org.lfenergy.shapeshifter.core.service.handler;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.model.OutgoingUftpMessage;

public interface UftpPayloadHandler {

  void notifyNewIncomingMessage(IncomingUftpMessage<? extends PayloadMessageType> message);

  void notifyNewOutgoingMessage(OutgoingUftpMessage<? extends PayloadMessageType> message);
}
