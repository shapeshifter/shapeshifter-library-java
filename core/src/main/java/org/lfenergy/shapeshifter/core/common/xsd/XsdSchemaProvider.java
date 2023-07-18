// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

@CommonsLog
@RequiredArgsConstructor
public class XsdSchemaProvider {

  // Not using java.net.URL as key is deliberate since URL#equals() is known for its dependency on DNS resolution
  private static final Map<String, XsdSchemaPool> cache = new HashMap<>();

  private final XsdFactory xsdFactory;

  public Validator getValidator(final URL xsd) {
    Schema schema = null;
    try {
      schema = claim(xsd);

      try {
        var validator = schema.newValidator();

        // Disable access to external entities in XML parsing to prevent XXE attacks
        // See: https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#validator
        validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        return validator;
      } catch (SAXNotRecognizedException | SAXNotSupportedException e) {
        throw new UftpConnectorException("Could not create XML validator", e);
      }
    } finally {
      release(xsd, schema);
    }
  }

  private Schema claim(final URL xsd) {
    return cache.computeIfAbsent(xsd.toString(), key -> xsdFactory.newXsdSchemaPool(xsd))
                .claim();
  }

  private void release(final URL xsd, Schema schema) {
    cache.get(xsd.toString()).release(schema);
  }
}
