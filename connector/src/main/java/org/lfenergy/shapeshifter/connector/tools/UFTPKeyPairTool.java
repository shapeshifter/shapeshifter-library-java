package org.lfenergy.shapeshifter.connector.tools;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import java.util.Base64;
import lombok.val;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;

public class UFTPKeyPairTool {

  public static void main(String[] args) {
    System.out.println("GOPACS UFTP Key Pair Tool");
    System.out.println("-------------------------");
    System.out.println("Generating key pair...");

    val keypair = generateKeyPair();

    System.out.println();
    System.out.println("Private Key:");
    System.out.println();
    System.out.println(keypair.privateKey());

    System.out.println();
    System.out.println("Public Key:");
    System.out.println();
    System.out.println(keypair.publicKey());

    System.out.println();
    System.out.println("All done!");
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
