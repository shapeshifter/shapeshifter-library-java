// SPDX-FileCopyrightText: 2020-2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package eu.uftplib.service;

public interface UftpService {
    
    /**
     * Send and XML message
     * @param message   The XML message
     * @return          The message id
     */
    Long sendMessage(String message);

    /**
     * Query a message on the message id
     * @param id        The message id
     * @return          The XML message
     */
    String queryMessage(Long id);

    /**
     * The housekeeping of the library. Call this function the initiate the resending of failed messages
     */
    void houseKeeping();

    /**
     * Call the notification callbacks when a new message arrives
     * @param message   The new message id
     * @param xml       The new XML message
     */
    void notifyNewMessage(Long message, String xml);

    /**
     * Add a new callback handler for new messages
     * @param newMessageListener    The callback function
     */
    void addNewMessageListener(NewMessageListener newMessageListener);

    /**
     * Call the notification callbacks to inform over the delivery status
     * @param message   The message id
     * @param status    The status of the delivery of the message
     */
    void notifyDeliveryStatus(Long message, DeliveryStatus status);

    /**
     * Add a new callback handler for new messages
     * @param deliveryStatusListener    The callback function
     */
    void addDeliveryStatusListener(DeliveryStatusListener deliveryStatusListener);
}
