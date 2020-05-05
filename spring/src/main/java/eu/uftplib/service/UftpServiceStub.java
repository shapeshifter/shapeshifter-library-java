package eu.uftplib.service;

public class UftpServiceStub implements UftpService {
    public void SendMessage() {
        System.out.println("Message Sent");
    }

    public String QueryMessage(long id) {
        System.out.println("Message Queried with id " + id);
        return "MessageWithId" + id;
    }
    
}