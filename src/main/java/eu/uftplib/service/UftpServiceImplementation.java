package eu.uftplib.service;

import java.util.ArrayList;
import java.util.List;

import eu.uftplib.entity.Message;
import eu.uftplib.repository.MessageRepository;

public class UftpServiceImplementation implements UftpService {

    private MessageRepository messageRepository;
    private UftpSendMessageService uftpSendMessageService;
    private UftpValidationService uftpValidationService;
    private String privateKey;
    private Long retryCount;

    private List<NewMessageListener> newMessageListeners = new ArrayList<NewMessageListener>();
    private List<DeliveryStatusListener> deliveryStatusListeners = new ArrayList<DeliveryStatusListener>();

    public UftpServiceImplementation(MessageRepository messageRepository, UftpSendMessageService uftpSendMessageService, UftpValidationService uftpValidationService, String privateKey, Long retryCount) {
        this.messageRepository = messageRepository;
        this.uftpSendMessageService = uftpSendMessageService;
        this.uftpValidationService = uftpValidationService;
        this.privateKey = privateKey;
        this.retryCount = retryCount;
    }

    public Long sendMessage(String message) {
        var domainPair = uftpValidationService.validateXml(message, MessageDirection.Outgoing);
        var m = messageRepository.save(new Message(message, domainPair.getSenderDomain(), domainPair.getRecipientDomain(), false, true, 0L, false));
        sendMessageInternal(m, privateKey, retryCount);
        return m.getId();
    }

    public String queryMessage(Long id) {
        System.out.println("Message Queried with id " + id);
        return "MessageWithId" + id;
    }

    public void houseKeeping() {
        System.out.println("Housekeeping..");
        var messages = messageRepository.findRetryMessages(retryCount);
        for (var message : messages) {
            System.out.println(message.getId());
            sendMessageInternal(message, privateKey, retryCount);
        }
    }

    public void addNewMessageListener(NewMessageListener newMessageListener) {
        newMessageListeners.add(newMessageListener);
    }

    public void notifyNewMessage(Long message, String xml) {
        for (NewMessageListener newMessageListener : newMessageListeners){
            newMessageListener.newMessage(message, xml);
        }
    }

    public void addDeliveryStatusListener(DeliveryStatusListener deliveryStatusListener) {
        deliveryStatusListeners.add(deliveryStatusListener);
    }

    public void notifyDeliveryStatus(Long message, DeliveryStatus status) {
        for (DeliveryStatusListener deliveryStatusListener : deliveryStatusListeners){
            deliveryStatusListener.deliveryStatus(message, status);
        }
    }

    private boolean sendMessageInternal(Message message, String privateKey, Long retryCount) {
        if (uftpSendMessageService.sendMessage(message.getMessage(), privateKey, new DomainPair(message.getSenderDomain(), message.getRecipientDomain()))) {
            messageRepository.setSuccessfullSendById(message.getId(), true);
            notifyDeliveryStatus(message.getId(), DeliveryStatus.Send);
            return true;
        } else {
            messageRepository.setRetryCountById(message.getId(), message.getRetryCount()+1);
            if (message.getRetryCount()+1 < retryCount) {
                notifyDeliveryStatus(message.getId(), DeliveryStatus.Retrying);
            } else {
                notifyDeliveryStatus(message.getId(), DeliveryStatus.Failed);
            }
        }
        return false;
    }
}
