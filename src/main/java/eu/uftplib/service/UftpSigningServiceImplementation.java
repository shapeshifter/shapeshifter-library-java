package eu.uftplib.service;

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.utils.Key;

public class UftpSigningServiceImplementation implements UftpSigningService {
    public String sealMessage(String message, String privateKey) {
        LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
        try {
            return lazySodium.cryptoSign(message, Key.fromHexString(privateKey));
        } catch (SodiumException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String unsealMessage(String message, String publicKey) {
        LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
        return lazySodium.cryptoSignOpen(message, Key.fromHexString(publicKey));
    }

    public KeyPair generateKeyPair() {
        LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());
        try {
            var keyPair = lazySodium.cryptoSignKeypair();
            return new KeyPair(keyPair.getPublicKey().getAsHexString(), keyPair.getSecretKey().getAsHexString());
        } catch (SodiumException e) {
            e.printStackTrace();
        }
        return null;
    }
}
