package org.lfenergy.shapeshifter.connector.service.sending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.ShippingDetails;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UftpSendMessageService {

  private final UftpSerializer serializer;
  private final UftpCryptoService cryptoService;
  private final ParticipantResolutionService participantService;
  private final UftpSendFactory factory;

  public <T extends PayloadMessageType> void attemptToSendMessage(T payloadMessage, ShippingDetails details) {
    String signedXml = getSignedXml(payloadMessage, details);
    send(signedXml, details.recipient());
  }

  private <T extends PayloadMessageType> String getSignedXml(T payloadMessage, ShippingDetails details) {
    var payloadXml = serializer.toXml(payloadMessage);
    var signedMessage = cryptoService.sealMessage(payloadXml, details.sender(), details.senderPrivateKey());
    return serializer.toXml(signedMessage);
  }

  private void send(String signedXml, UftpParticipant recipient) {
    var url = participantService.getEndPointUrl(recipient);

    try {
      log.debug("Sending message to: {}",  url);

      ResponseEntity<String> response = factory.newRestTemplate().postForEntity(
          url, request(signedXml), String.class
      );

      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new UftpConnectorException("Response status code: " + response.getStatusCode().value() + " - Details: " + response.getBody());
      }

    } catch (Exception cause) {
      throw new UftpConnectorException("Failed to send message to " + recipient + " at " + url, cause);
    }
  }

  private HttpEntity<String> request(String signedXml) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);

    return new HttpEntity<>(signedXml, headers);
  }
}
