// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.core.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.core.common.xsd.XsdFactory;
import org.lfenergy.shapeshifter.core.common.xsd.XsdSchemaFactoryPool;
import org.lfenergy.shapeshifter.core.common.xsd.XsdSchemaProvider;
import org.lfenergy.shapeshifter.core.common.xsd.XsdValidator;
import org.lfenergy.shapeshifter.core.service.crypto.LazySodiumBase64Pool;
import org.lfenergy.shapeshifter.core.service.crypto.LazySodiumFactory;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;

@Slf4j
public class UftpVerifySignedMessageTool {

  static void usage() {
    log.info("Usage: " + UftpVerifySignedMessageTool.class.getSimpleName() + " <input file> <output file> <public key>");
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      usage();
      return;
    }

    var inputFileName = args[0];
    var outputFileName = args[1];
    var publicKey = args[2];

    var xsdSchemaFactoryPool = new XsdSchemaFactoryPool();
    var xsdFactory = new XsdFactory(xsdSchemaFactoryPool);
    var xsdSchemaProvider = new XsdSchemaProvider(xsdFactory);
    var xsdValidator = new XsdValidator(xsdSchemaProvider);
    var xmlSerializer = new XmlSerializer();

    var lazySodiumFactory = new LazySodiumFactory();
    var lazySodiumInstancePool = new LazySodiumBase64Pool();

    var participantResolutionService = new ParticipantResolutionService((role, domain) -> Optional.empty());

    var uftpCryptoService = new UftpCryptoService(participantResolutionService, lazySodiumFactory, lazySodiumInstancePool);
    var uftpSerializer = new UftpSerializer(xmlSerializer, xsdValidator);

    try {
      var signedXml = Files.readString(Paths.get(inputFileName));
      var signedMessage = uftpSerializer.fromSignedXml(signedXml);

      var payloadXml = uftpCryptoService.verifySignedMessage(signedMessage, publicKey);

      Files.writeString(Paths.get(outputFileName), payloadXml);
    } catch (IOException e) {
      log.error("Could not verify message: " + e.getMessage(), e);
    }
  }
}
