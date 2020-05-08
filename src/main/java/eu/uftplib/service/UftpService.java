package eu.uftplib.service;

public interface UftpService {
    Long sendMessage(String message);
    String queryMessage(Long id);
}
