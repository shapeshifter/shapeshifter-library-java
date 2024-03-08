// Copyright 2024 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.model;

import org.lfenergy.shapeshifter.api.PayloadMessageType;

public final class OutgoingUftpMessage<T extends PayloadMessageType> extends UftpMessage<T> {

    private OutgoingUftpMessage(UftpParticipant sender, T payloadMessage) {
        super(sender, payloadMessage);
    }

    public static <T extends PayloadMessageType> OutgoingUftpMessage<T> create(UftpParticipant sender, T payloadMessage) {
        return new OutgoingUftpMessage<>(sender, payloadMessage);
    }

    @Override
    public UftpMessageDirection direction() {
        return UftpMessageDirection.OUTGOING;
    }
}
