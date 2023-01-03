package org.lfenergy.shapeshifter.connector.model;

public record ShippingDetails(UftpParticipant sender, String senderPrivateKey, UftpParticipant recipient) {

}
