// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.common.xml;

import jakarta.xml.bind.JAXBException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
@RequiredArgsConstructor
@Slf4j
public class XmlSerializer {

  private final JAXBTools jaxbTools;
  private final XmlFactory factory;

  private static final SAXParserFactory SAX_PARSER_FACTORY = saxParserFactory();

  public <T> String toXml(final T object) {
    try {
      final StringWriter xmlWriter = factory.newStringWriter();
      jaxbTools.createMarshaller(object.getClass()).marshal(object, xmlWriter);
      return xmlWriter.toString();
    } catch (final Exception cause) {
      throw new UftpConnectorException("Failed to serialize " + object.getClass().getSimpleName() + " instance to XML.", cause);
    }
  }

  public <T> T fromXml(String xmlString, Class<T> typeToUnmarshal) {
    try {
      var xmlSource = new SAXSource(SAX_PARSER_FACTORY.newSAXParser().getXMLReader(),
                                    new InputSource(new StringReader(xmlString)));
      var unmarshaller = jaxbTools.createUnmarshaller(typeToUnmarshal);
      return typeToUnmarshal.cast(unmarshaller.unmarshal(xmlSource));
    } catch (JAXBException | ParserConfigurationException | SAXException cause) {
      throw new UftpConnectorException("Failed to unmarshal XML to " + typeToUnmarshal.getSimpleName() + " instance.", cause);
    }
  }

  /**
   * Creates a SAX Parser factory with some measures to prevent XML External Entity vulnerabilities Taken from: <a
   * href="https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#jaxb-unmarshaller">...</a>
   *
   * @return a SAXParserFactory
   */
  private static SAXParserFactory saxParserFactory() {
    try {
      var saxParserFactory = SAXParserFactory.newInstance();
      saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      saxParserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      saxParserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      return saxParserFactory;
    } catch (ParserConfigurationException | SAXException ex) {
      throw new UftpConnectorException("Failed to to create a SAXParserFactory: " + ex.getMessage(), ex);
    }
  }
}
