// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.model;

public record SigningDetails(UftpParticipant sender, String senderPrivateKey, UftpParticipant recipient) {

}
