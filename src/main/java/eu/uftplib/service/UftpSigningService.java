package eu.uftplib.service;

public interface UftpSigningService {
    String sealMessage(String message, String privateKey);
    String unsealMessage(String message, String publicKey);
    KeyPair generateKeyPair();
}
