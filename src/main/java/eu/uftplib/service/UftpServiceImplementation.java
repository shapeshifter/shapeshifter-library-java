package eu.uftplib.service;

public class UftpServiceImplementation implements UftpService {
    public void sendMessage() {
        System.out.println("Message Sent");
    }

    public String queryMessage(long id) {
        System.out.println("Message Queried with id " + id);
        return "MessageWithId" + id;
    }

}
