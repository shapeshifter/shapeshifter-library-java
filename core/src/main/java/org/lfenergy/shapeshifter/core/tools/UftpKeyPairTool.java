// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.tools;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import com.goterl.lazysodium.exceptions.SodiumException;
import java.util.Base64;
import lombok.val;
import org.apache.commons.logging.LogFactory;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

public class UftpKeyPairTool {

  public static void main(String[] args) {
    var log = LogFactory.getLog(UftpKeyPairTool.class);

    log.info("GOPACS UFTP Key Pair Tool");
    log.info("-------------------------");

    val keypair = generateKeyPair();

    log.info("Private Key: " + keypair.privateKey());
    log.info("Public Key : " + keypair.publicKey());
  }

  public static KeyPair generateKeyPair() {
    LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
    try {
      com.goterl.lazysodium.utils.KeyPair keyPair = lazySodium.cryptoSignKeypair();
      String base64Public = Base64.getEncoder().encodeToString(keyPair.getPublicKey().getAsBytes());
      String base64Secret = Base64.getEncoder().encodeToString(keyPair.getSecretKey().getAsBytes());
      return new KeyPair(base64Public, base64Secret);
    } catch (SodiumException cause) {
      throw new UftpConnectorException("Failed to generate key pair.", cause);
    }
  }
}
