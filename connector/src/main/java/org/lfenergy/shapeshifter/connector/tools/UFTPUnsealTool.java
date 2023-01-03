package org.lfenergy.shapeshifter.connector.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.connector.common.xml.JAXBTools;
import org.lfenergy.shapeshifter.connector.common.xml.XmlFactory;
import org.lfenergy.shapeshifter.connector.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.connector.common.xsd.XsdFactory;
import org.lfenergy.shapeshifter.connector.common.xsd.XsdSchemaFactoryPool;
import org.lfenergy.shapeshifter.connector.common.xsd.XsdSchemaProvider;
import org.lfenergy.shapeshifter.connector.common.xsd.XsdValidator;
import org.lfenergy.shapeshifter.connector.service.crypto.LazySodiumBase64Pool;
import org.lfenergy.shapeshifter.connector.service.crypto.LazySodiumFactory;
import org.lfenergy.shapeshifter.connector.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.connector.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.connector.service.serialization.UftpSerializer;

@Slf4j
public class UFTPUnsealTool {

  static void usage() {
    log.info("Usage: " + UFTPUnsealTool.class.getSimpleName() + " <input file> <output file> <public key>");
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      usage();
      return;
    }

    var inputFileName = args[0];
    var outputFileName = args[1];
    var publicKey = args[2];

    var xmlFactory = new XmlFactory();
    var xsdSchemaFactoryPool = new XsdSchemaFactoryPool();
    var xsdFactory = new XsdFactory(xsdSchemaFactoryPool);
    var xsdSchemaProvider = new XsdSchemaProvider(xsdFactory);
    var xsdValidator = new XsdValidator(xmlFactory, xsdSchemaProvider);
    var xmlSerializer = new XmlSerializer(new JAXBTools(), xmlFactory);

    var lazySodiumFactory = new LazySodiumFactory();
    var lazySodiumInstancePool = new LazySodiumBase64Pool();

    var participantResolutionService = new ParticipantResolutionService((role, domain) -> Optional.empty());

    var uftpCryptoService = new UftpCryptoService(participantResolutionService, lazySodiumFactory, lazySodiumInstancePool);
    var uftpSerializer = new UftpSerializer(xmlSerializer, xsdValidator);

    try {
      var signedXml = Files.readString(Paths.get(inputFileName));
      var signedMessage = uftpSerializer.fromSignedXml(signedXml);

      var payloadXml = uftpCryptoService.unsealMessage(signedMessage, publicKey);

      Files.writeString(Paths.get(outputFileName), payloadXml);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
