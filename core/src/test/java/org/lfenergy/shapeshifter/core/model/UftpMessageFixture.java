// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.model;

import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;

public final class UftpMessageFixture {

    private UftpMessageFixture() {
        // Utility class
    }

    public static <T extends PayloadMessageType> UftpMessage<T> createIncoming(UftpParticipant sender, T payloadMessage) {
        return UftpMessage.createIncoming(sender, payloadMessage, "<SignedMessage/>", "<Payload/>");
    }

    public static <T extends PayloadMessageResponseType> UftpMessage<T> createIncomingResponse(UftpParticipant sender, T payloadMessage) {
        return UftpMessage.createIncoming(sender, payloadMessage,"<SignedMessage/>", "<Payload/>");
    }

    public static <T extends PayloadMessageType> UftpMessage<T> createOutgoing(UftpParticipant sender, T payloadMessage) {
        return UftpMessage.createOutgoing(sender, payloadMessage);
    }

}
