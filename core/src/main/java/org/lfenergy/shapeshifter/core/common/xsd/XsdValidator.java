// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.core.common.HttpStatusCode;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.xml.sax.SAXException;

@RequiredArgsConstructor
public class XsdValidator {

  private final XsdSchemaProvider schemaProvider;

  public void validate(final String xml, URL xsd) {
    final Source xmlSource = new StreamSource(new StringReader(xml));
    try {
      final Validator validator = schemaProvider.getValidator(xsd);
      validator.validate(xmlSource);
    } catch (SAXException | IOException cause) {
      throw new UftpConnectorException("XSD validation failed: " + cause.getMessage(), HttpStatusCode.BAD_REQUEST, cause);
    }
  }
}
