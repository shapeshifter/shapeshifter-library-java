package org.lfenergy.shapeshifter.connector.service.receiving;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UftpInternalController {

  private final UftpSerializer deserializer;
  private final UftpCryptoService uftpCryptoService;
  private final ReceivedMessageProcessor processor;
  private final UftpErrorProcessor errorProcessor;

  // TODO: Docs 'App 4, paragraph 4, Error Handling': Produce 400 upon invalid MediaType. Now it will be 415.
  //       Debatable, since 415 is the correct response and not 400. Also, 400 is used for other reasons as well
  //       obscuring the actual error.
  @PostMapping(value = "/USEF/2019/SignedMessage",
      consumes = MediaType.TEXT_XML_VALUE,
      produces = MediaType.TEXT_XML_VALUE
  )
  public ResponseEntity<String> receiveUftpMessage(@RequestBody String transportXml) {
    try {
      log.debug("Received UFTP message.");
      var signedMessage = deserializer.fromSignedXml(transportXml);
      log.info("Received UFTP message from " + signedMessage.getSenderDomain());

      var payloadXml = uftpCryptoService.unsealMessage(signedMessage);
      log.debug("Received UFTP message unsealed.");
      var payloadMessage = deserializer.fromPayloadXml(payloadXml);

      processor.onReceivedMessage(signedMessage, payloadMessage);

      return ResponseEntity.ok(null);
    } catch (UftpConnectorException cause) {
      return handleException(transportXml, cause);
    } catch (Exception cause) {
      return handleException(transportXml, new UftpConnectorException(cause.getMessage(), cause));
    }
  }

  private ResponseEntity<String> handleException(String transportXml, UftpConnectorException cause) {
    String error = "Failed to process received UFTP message. Error: " + cause.getMessage();
    errorProcessor.onErrorDuringReceivedMessageReading(transportXml, cause);
    return ResponseEntity.status(cause.getHttpStatusCode()).body(error);
  }
}