package org.lfenergy.shapeshifter.core.service.handler;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;

public interface UftpPayloadHandler {

  void notifyNewIncomingMessage(UftpParticipant from, PayloadMessageType message);

  void notifyNewOutgoingMessage(UftpParticipant from, PayloadMessageType message);

}
