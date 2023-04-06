// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.tools;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;

@Slf4j
public class UftpKeyPairTool {

  public static void main(String[] args) {
    log.info("GOPACS UFTP Key Pair Tool");
    log.info("-------------------------");
    log.info("Generating key pair...");

    val keypair = generateKeyPair();

    log.info("\n");
    log.info("Private Key:");
    log.info("\n");
    log.info(keypair.privateKey());

    log.info("\n");
    log.info("Public Key:");
    log.info("\n");
    log.info(keypair.publicKey());

    log.info("\n");
    log.info("All done!");
  }

  public static KeyPair generateKeyPair() {
    LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
    try {
      com.goterl.lazysodium.utils.KeyPair keyPair = lazySodium.cryptoSignKeypair();
      String base64Public = Base64.getEncoder().encodeToString(keyPair.getPublicKey().getAsBytes());
      String base64Secret = Base64.getEncoder().encodeToString(keyPair.getSecretKey().getAsBytes());
      return new KeyPair(base64Public, base64Secret);
    } catch (Exception cause) {
      throw new UftpConnectorException("Failed to generate key pair.", cause);
    }
  }
}
