package eu.uftplib.service;

public interface NewMessageListener {
    void newMessage(Long message, String xml);
}