// Copyright 2024 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.model;

import org.lfenergy.shapeshifter.api.PayloadMessageType;

public final class IncomingUftpMessage<T extends PayloadMessageType> extends UftpMessage<T> {

    private final String signedMessageXml;
    private final String payloadMessageXml;

    private IncomingUftpMessage(UftpParticipant sender, T payloadMessage, String signedMessageXml, String payloadMessageXml) {
        super(sender, payloadMessage);
        this.signedMessageXml = signedMessageXml;
        this.payloadMessageXml = payloadMessageXml;
    }

    public static <T extends PayloadMessageType> IncomingUftpMessage<T> create(UftpParticipant sender, T payloadMessage, String signedMessageXml, String payloadMessageXml) {
        return new IncomingUftpMessage<>(sender, payloadMessage, signedMessageXml, payloadMessageXml);
    }

    @Override
    public UftpMessageDirection direction() {
        return UftpMessageDirection.INCOMING;
    }

    public String signedMessageXml() {
        return signedMessageXml;
    }

    public String payloadMessageXml() {
        return payloadMessageXml;
    }
}
