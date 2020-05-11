package eu.uftplib.service;

public interface UftpService {
    Long sendMessage(String message);
    String queryMessage(Long id);

    void houseKeeping();

    void notifyNewMessage(String message);
    void addNewMessageListener(NewMessageListener newMessageListener);
    void notifyDeliveryStatus(Long message, DeliveryStatus status);
    void addDeliveryStatusListener(DeliveryStatusListener deliveryStatusListener);
}
