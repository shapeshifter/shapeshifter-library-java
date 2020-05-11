package eu.uftplib.service;

public interface UftpSendMessageService {
    boolean sendMessage(String xml, String endpoint);
}
