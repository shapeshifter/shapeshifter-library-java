// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.serialization;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.xsdinfo.UftpXsds;
import org.lfenergy.shapeshifter.core.common.HttpStatusCode;
import org.lfenergy.shapeshifter.core.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.core.common.xsd.XsdValidator;

@RequiredArgsConstructor
public class UftpSerializer {

  private final XmlSerializer serializer;
  private final XsdValidator xsdValidator;

  public SignedMessage fromSignedXml(String signedXml) {
    validate(signedXml, UftpXsds.COMMON);

    try {
      return serializer.fromXml(signedXml, SignedMessage.class);
    } catch (Exception cause) {
      throw new UftpSerializerException("SignedMessage XML deserialization failed: " + cause.getMessage(), cause, HttpStatusCode.BAD_REQUEST);
    }
  }

  public PayloadMessageType fromPayloadXml(String payloadXml) {
    validate(payloadXml, UftpXsds.ALL);

    try {
      return serializer.fromXml(payloadXml, PayloadMessageType.class);
    } catch (Exception cause) {
      throw new UftpSerializerException("Payload message XML deserialization failed: " + cause.getMessage(), cause, HttpStatusCode.BAD_REQUEST);
    }
  }

  public String toXml(SignedMessage signedMessage) {
    String signedXml = serializer.toXml(signedMessage);
    validate(signedXml, UftpXsds.COMMON);
    return signedXml;
  }

  public <T extends PayloadMessageType> String toXml(T payloadMessage) {
    String payloadXml = serializer.toXml(payloadMessage);
    validate(payloadXml, UftpXsds.ALL);
    return payloadXml;
  }

  private void validate(String xml, UftpXsds xsd) {
    xsdValidator.validate(xml, xsd.getUrl());
  }

}
