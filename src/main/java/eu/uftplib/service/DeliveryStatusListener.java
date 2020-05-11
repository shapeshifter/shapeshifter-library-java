package eu.uftplib.service;

public interface DeliveryStatusListener {
    void deliveryStatus(Long message, DeliveryStatus status);
}