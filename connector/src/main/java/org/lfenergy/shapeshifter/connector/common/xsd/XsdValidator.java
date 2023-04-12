// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.common.xsd;

import java.io.IOException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.common.xml.XmlFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
@RequiredArgsConstructor
public class XsdValidator {

  private final XmlFactory factory;
  private final XsdSchemaProvider schemaProvider;

  public void validate(final String xml, URL xsd) {
    final Source xmlSource = factory.newStringSource(xml);
    try {
      final Validator validator = schemaProvider.getValidator(xsd);
      validator.validate(xmlSource);
    } catch (SAXException | IOException cause) {
      throw new UftpConnectorException("XSD validation failed: " + cause.getMessage(), cause, HttpStatus.BAD_REQUEST);
    }
  }
}
