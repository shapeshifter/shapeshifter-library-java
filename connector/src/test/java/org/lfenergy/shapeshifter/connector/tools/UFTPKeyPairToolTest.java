// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.crypto.LazySodiumBase64Pool;
import org.lfenergy.shapeshifter.connector.service.crypto.LazySodiumFactory;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;

class UftpKeyPairToolTest {

  @Test
  void generateKeyPair() {
    val originalMessage = "This is a TEST!";
    val cryptoService = new UftpCryptoService(null, new LazySodiumFactory(), new LazySodiumBase64Pool());

    val keypair = UftpKeyPairTool.generateKeyPair();
    val signedMessage = cryptoService.signMessage(
        originalMessage,
        new UftpParticipant("test.class", USEFRoleType.AGR),
        keypair.privateKey());
    val roundTripMessage = cryptoService.verifySignedMessage(signedMessage, keypair.publicKey());

    assertEquals(originalMessage, roundTripMessage);
    assertNotEquals(originalMessage, new String(signedMessage.getBody()));
  }
}