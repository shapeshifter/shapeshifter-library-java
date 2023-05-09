// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import org.lfenergy.shapeshifter.core.common.collection.AbstractInstancePool;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.xml.sax.SAXException;

public class XsdSchemaFactoryPool extends AbstractInstancePool<SchemaFactory> {

  @SuppressWarnings("java:S2755")
  @Override
  protected SchemaFactory create() {
    // Sonar warning: XML parsers should not be vulnerable to XXE attacks
    // We must allow for XSD's that are loaded from the local classpath (either through file or in a jar)
    //
    // We will disable all DOCTYPE declarations inside the (incoming) XML documents
    var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      schemaFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "jar:file,file");
    } catch (SAXException ex) {
      throw new UftpConnectorException("Failed to to create a SchemaFactory: " + ex.getMessage(), ex);
    }
    return schemaFactory;
  }
}
