// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.sending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.SigningDetails;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageDirection;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidationService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Sends UFTP messages to recipients
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UftpSendMessageService {

  private final UftpSerializer serializer;
  private final UftpCryptoService cryptoService;
  private final ParticipantResolutionService participantService;
  private final UftpSendFactory factory;
  private final UftpValidationService uftpValidationService;

  /**
   * Attempts to send a message, without validation
   *
   * @throws UftpSendException if sending fails
   */
  public void attemptToSendMessage(PayloadMessageType payloadMessage, SigningDetails details) throws UftpSendException {
    doSend(payloadMessage, details);
  }

  /**
   * Attempts to send a message, with validation
   *
   * @throws UftpSendException if validation fails, or if sending fails
   */
  public void attemptToValidateAndSendMessage(PayloadMessageType payloadMessage, SigningDetails details) throws UftpSendException {
    // We will validate outgoing messages, but we will not validate outgoing response messages.
    if (!(payloadMessage instanceof PayloadMessageResponseType)) {
      var uftpMessage = new UftpMessage<>(details.sender(), UftpMessageDirection.OUTGOING, payloadMessage);
      var validationResult = uftpValidationService.validate(uftpMessage);
      if (!validationResult.valid()) {
        throw new UftpSendException(
            "Could not send UFTP message; the outgoing " + payloadMessage.getClass().getSimpleName() + " message was not valid: " + validationResult.rejectionReason());
      }
    }
    doSend(payloadMessage, details);
  }

  private void doSend(PayloadMessageType payloadMessage, SigningDetails details) {
    String signedXml = getSignedXml(payloadMessage, details);
    send(signedXml, details.recipient());
  }

  private String getSignedXml(PayloadMessageType payloadMessage, SigningDetails details) {
    var payloadXml = serializer.toXml(payloadMessage);
    var signedMessage = cryptoService.signMessage(payloadXml, details.sender(), details.senderPrivateKey());
    return serializer.toXml(signedMessage);
  }

  private void send(String signedXml, UftpParticipant recipient) {
    var url = participantService.getEndPointUrl(recipient);

    try {
      log.debug("Sending message to: {}", url);

      ResponseEntity<String> response = factory.newRestTemplate().postForEntity(
          url, request(signedXml), String.class
      );

      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new UftpSendException("Response status code: " + response.getStatusCode().value() + " - Details: " + response.getBody());
      }

    } catch (HttpClientErrorException e) {
      throw new UftpClientErrorException(e.getStatusCode(), "Failed to send message to " + recipient.domain() + " at " + url, e);
    } catch (Exception cause) {
      throw new UftpSendException("Failed to send message to " + recipient.domain() + " at " + url, cause);
    }
  }

  private HttpEntity<String> request(String signedXml) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_XML);

    return new HttpEntity<>(signedXml, headers);
  }
}
