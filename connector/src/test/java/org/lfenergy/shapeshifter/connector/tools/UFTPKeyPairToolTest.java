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

class UFTPKeyPairToolTest {

  @Test
  void generateKeyPair() {
    val originalMessage = "This is a TEST!";
    val cryptoService = new UftpCryptoService(null, new LazySodiumFactory(), new LazySodiumBase64Pool());

    val keypair = UFTPKeyPairTool.generateKeyPair();
    val signedMessage = cryptoService.sealMessage(
        originalMessage,
        new UftpParticipant("test.class", USEFRoleType.AGR),
        keypair.privateKey());
    val roundTripMessage = cryptoService.unsealMessage(signedMessage, keypair.publicKey());

    assertEquals(originalMessage, roundTripMessage);
    assertNotEquals(originalMessage, new String(signedMessage.getBody()));
  }
}