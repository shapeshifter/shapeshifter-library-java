// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.crypto;

import com.goterl.lazysodium.LazySodiumJava;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.participant.ParticipantResolutionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UftpCryptoService {

  private final ParticipantResolutionService participantService;
  private final LazySodiumFactory factory;
  private final LazySodiumBase64Pool lazySodiumInstancePool;

  public SignedMessage signMessage(String payloadXml, UftpParticipant sender, String privateKey) {
    LazySodiumJava lazySodium = null;
    try {
      lazySodium = lazySodiumInstancePool.claim();
      String base64Body = lazySodium.cryptoSign(payloadXml, privateKey);

      SignedMessage signedMessage = new SignedMessage();
      signedMessage.setSenderDomain(sender.domain());
      signedMessage.setSenderRole(sender.role());
      signedMessage.setBody(Base64.getDecoder().decode(base64Body));

      return signedMessage;
    } catch (Exception cause) {
      throw new UftpConnectorException("Failed to sign message.", cause);
    } finally {
      lazySodiumInstancePool.release(lazySodium);
    }
  }

  public String verifySignedMessage(SignedMessage signedMessage) {
    try {
      String publicKey = participantService.getPublicKey(signedMessage.getSenderRole(), signedMessage.getSenderDomain());
      return verifySignedMessage(signedMessage, publicKey);
    } catch (Exception cause) {
      throw new UftpConnectorException("Failed to verify message.", HttpStatus.UNAUTHORIZED, cause);
    }
  }

  public String verifySignedMessage(SignedMessage signedMessage, String publicKey) {
    LazySodiumJava lazySodium = null;
    try {
      String base64Body = Base64.getEncoder().encodeToString(signedMessage.getBody());
      lazySodium = lazySodiumInstancePool.claim();

      var unsealed = lazySodium.cryptoSignOpen(base64Body, factory.keyFromBase64String(publicKey));

      if (unsealed == null) {
        throw new UftpConnectorException("Failed to verify message. Message is not validly signed for given public key.");
      }

      return unsealed;
    } finally {
      lazySodiumInstancePool.release(lazySodium);
    }
  }
}
