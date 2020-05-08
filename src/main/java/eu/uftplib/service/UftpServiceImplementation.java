package eu.uftplib.service;

import java.util.ArrayList;
import java.util.List;

import eu.uftplib.entity.Message;
import eu.uftplib.repository.MessageRepository;

public class UftpServiceImplementation implements UftpService {

    private MessageRepository messageRepository;

    private List<NewMessageListener> newMessageListeners = new ArrayList<NewMessageListener>();

    public void addNewMessageListener(NewMessageListener newMessageListener) {
        newMessageListeners.add(newMessageListener);
    }

    public void notifyNewMessage(String message) {
        for (NewMessageListener newMessageListener : newMessageListeners){
            newMessageListener.newMessage(message);
        }
    }

    public UftpServiceImplementation(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Long sendMessage(String message) {
        var m = messageRepository.save(new Message(message, true, false, 0L, false));
        System.out.println("Message Sent");
        return m.getId();
    }

    public String queryMessage(Long id) {
        System.out.println("Message Queried with id " + id);
        return "MessageWithId" + id;
    }

    public void houseKeeping() {

    }
}
