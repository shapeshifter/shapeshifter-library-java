package eu.uftplib.service;

public interface UftpSigningService {
    String sealMessage(String message, String privateKey, DomainPair domainPair);
    String unsealMessage(String message, String publicKey);
    KeyPair generateKeyPair();
}
