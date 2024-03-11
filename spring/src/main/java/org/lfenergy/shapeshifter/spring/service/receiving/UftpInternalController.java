// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.service.receiving;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.receiving.DuplicateMessageException;
import org.lfenergy.shapeshifter.core.service.receiving.ReceivedMessageProcessor;
import org.lfenergy.shapeshifter.core.service.receiving.UftpReceiveException;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Exposes an endpoint for sending a signed UFTP message to the receiving counterparty
 */
@CommonsLog
@RestController
@RequiredArgsConstructor
@RequestMapping("/shapeshifter/api/v3")
@Tag(name = "UFTP Message")
public class UftpInternalController {

  private static final String UFTP_MESSAGE_XML_EXAMPLE_STRING =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<SignedMessage SenderDomain=\"string\" SenderRole=\"string\" Body=\"string\"></SignedMessage>";
  private final UftpSerializer deserializer;
  private final UftpCryptoService uftpCryptoService;
  private final ReceivedMessageProcessor processor;
  private final UftpErrorProcessor errorProcessor;

  /**
   * Receives a signed UFTP message which will be sent to the receiving counterparty
   *
   * @param transportXml the signed UFTP message in XML format
   */
  @PostMapping(value = "/message", consumes = MediaType.TEXT_XML_VALUE)
  @Operation(summary = "Send an UFTP message", description = "Send an UFTP message in a signed XML document",
      responses = {
          @ApiResponse(responseCode = "200", description = "The UFTP message was succesfully received"),
          @ApiResponse(responseCode = "400", description = "Error during XML validation or deserialization"),
          @ApiResponse(responseCode = "401", description = "Failed to unseal message"),
          @ApiResponse(responseCode = "500", description = "Internal server error: An unexpected error occurred. Details are provided in the response body",
              content = @Content(schema = @Schema(implementation = String.class)))
      })
  public ResponseEntity<String> postUftpMessage(
      @RequestBody
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "UFTP message in a signed XML document format",
          content = @Content(schema = @Schema(implementation = SignedMessage.class), examples = @ExampleObject(value = UFTP_MESSAGE_XML_EXAMPLE_STRING)
          ))
      String transportXml) {
    try {
      log.debug("Received UFTP message.");
      var signedMessage = deserializer.fromSignedXml(transportXml);
      log.info(String.format("Received UFTP message from %s", signedMessage.getSenderDomain()));

      var payloadXml = uftpCryptoService.verifySignedMessage(signedMessage);
      log.debug("Received UFTP message unsealed.");
      var payloadMessage = deserializer.fromPayloadXml(payloadXml);

      processor.onReceivedMessage(IncomingUftpMessage.create(new UftpParticipant(signedMessage), payloadMessage, transportXml, payloadXml));

      return ResponseEntity.ok(null);
    } catch (DuplicateMessageException e) {
      return ResponseEntity.badRequest().body("Duplicate message");
    } catch (UftpConnectorException cause) {
      return handleException(transportXml, cause);
    } catch (Exception cause) {
      return handleException(transportXml, new UftpReceiveException(cause.getMessage(), cause));
    }
  }

  private ResponseEntity<String> handleException(String transportXml, UftpConnectorException cause) {
    String error = "Failed to process received UFTP message. Error: " + cause.getMessage();
    errorProcessor.onErrorDuringReceivedMessageReading(transportXml, cause);
    return ResponseEntity.status(cause.getHttpStatusCode().getValue()).body(error);
  }
}