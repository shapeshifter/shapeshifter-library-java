// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.sending;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.MessageFormat;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.common.HttpStatusCode;
import org.lfenergy.shapeshifter.core.model.SigningDetails;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpMessageDirection;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;

/**
 * Sends UFTP messages to recipients
 */
@Slf4j
public class UftpSendMessageService {

  private static final String MSG_VALIDATION_FAILED = "Could not send UFTP message; the outgoing {0} message was not valid: {1}";
  private static final String MSG_INVALID_ENDPOINT = "Could not send UFTP message; invalid endpoint: {0}";
  private static final String MSG_CLIENT_ERROR = "Client error {0} received while sending UFTP message to {1}: {2}";
  private static final String MSG_SERVER_ERROR = "Server error {0} received while sending UFTP message to {1}: {2}";
  private static final String MSG_UNEXPECTED_IO_ERROR = "Unexpected I/O exception while sending UFTP message to {0}: {1}";
  private static final String MSG_INTERRUPTED = "Interrupted while sending UFTP message to {0}: {1} ";

  private final UftpSerializer serializer;
  private final UftpCryptoService cryptoService;
  private final ParticipantResolutionService participantService;
  private final UftpValidationService uftpValidationService;
  private final HttpClient httpClient;

  public UftpSendMessageService(UftpSerializer serializer,
                                UftpCryptoService cryptoService,
                                ParticipantResolutionService participantService,
                                UftpValidationService uftpValidationService) {
    this(serializer, cryptoService, participantService, uftpValidationService, HttpClient.newHttpClient());
  }

  public UftpSendMessageService(UftpSerializer serializer,
                                UftpCryptoService cryptoService,
                                ParticipantResolutionService participantService,
                                UftpValidationService uftpValidationService,
                                HttpClient httpClient) {
    this.serializer = serializer;
    this.cryptoService = cryptoService;
    this.participantService = participantService;
    this.uftpValidationService = uftpValidationService;
    this.httpClient = httpClient;
  }

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
        throw new UftpSendException(MessageFormat.format(MSG_VALIDATION_FAILED, payloadMessage.getClass().getSimpleName(), validationResult.rejectionReason()));
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

      var request = HttpRequest.newBuilder()
                               .uri(new URI(url))
                               .POST(BodyPublishers.ofString(signedXml))
                               .setHeader("Content-Type", "text/xml")
                               .build();

      var response = httpClient.send(request, BodyHandlers.ofString());

      var httpStatusCode = HttpStatusCode.getByValue(response.statusCode());

      if (httpStatusCode.isClientError()) {
        throw new UftpClientErrorException(MessageFormat.format(MSG_CLIENT_ERROR, response.statusCode(), url, response.body()), httpStatusCode);
      } else if (httpStatusCode.isServerError()) {
        throw new UftpServerErrorException(MessageFormat.format(MSG_SERVER_ERROR, response.statusCode(), url, response.body()), httpStatusCode);
      }
    } catch (URISyntaxException | IllegalArgumentException e) {
      throw new UftpSendException(MessageFormat.format(MSG_INVALID_ENDPOINT, e.getMessage()), e);
    } catch (IOException e) {
      throw new UftpSendException(MessageFormat.format(MSG_UNEXPECTED_IO_ERROR, url, e.getMessage()), e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new UftpSendException(MessageFormat.format(MSG_INTERRUPTED, url, e.getMessage()), e);
    }
  }
}
