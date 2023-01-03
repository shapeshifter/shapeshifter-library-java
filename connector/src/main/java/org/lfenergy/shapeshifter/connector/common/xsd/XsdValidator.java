package org.lfenergy.shapeshifter.connector.common.xsd;

import java.io.IOException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.common.xml.XmlFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Slf4j
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
      log.info("XML is valid for xsd: " + xsd);
    } catch (SAXException | IOException cause) {
      throw new UftpConnectorException("Validation of XML for XSD failed: " + xsd, cause);
    }
  }
}
