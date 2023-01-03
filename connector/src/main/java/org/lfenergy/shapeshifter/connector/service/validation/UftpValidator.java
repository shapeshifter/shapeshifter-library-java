package org.lfenergy.shapeshifter.connector.service.validation;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;

interface UftpValidator<T extends PayloadMessageType> {

  boolean appliesTo(Class<? extends PayloadMessageType> clazz);

  boolean valid(UftpParticipant sender, T payloadMessage);

  String getReason();
}
