// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.model;

import org.lfenergy.shapeshifter.api.PayloadMessageType;

/**
 * Reference to a previously sent or received UFTP message.
 *
 * @param messageID The MessageID of the UFTP message.
 * @param direction The direction of the UFTP message, whether it's incoming or outgoing.
 * @param senderDomain The SenderDomain of the UFTP message.
 * @param recipientDomain The RecipientDomain of the UFTP message.
 * @param type The class of the payload message.
 * @param <T> The type of the payload message.
 */
public record UftpMessageReference<T extends PayloadMessageType>(
    String messageID,
    UftpMessageDirection direction,
    String senderDomain,
    String recipientDomain,
    Class<T> type
) {
}
