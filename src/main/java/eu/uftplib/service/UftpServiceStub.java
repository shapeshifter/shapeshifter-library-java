package eu.uftplib.service;

public class UftpServiceStub implements UftpService {
    public Long sendMessage(String message) {
        System.out.println("Message Sent");
        return 0L;
    }

    public String queryMessage(Long id) {
        System.out.println("Message Queried with id " + id);
        return "MessageWithId" + id;
    }
    
}