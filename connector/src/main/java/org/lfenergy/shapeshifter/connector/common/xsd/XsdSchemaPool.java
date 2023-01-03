package org.lfenergy.shapeshifter.connector.common.xsd;

import java.net.URL;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.connector.common.collection.AbstractInstancePool;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.xml.sax.SAXException;

@RequiredArgsConstructor
@Getter
public class XsdSchemaPool extends AbstractInstancePool<Schema> {

  private final URL xsd;
  private final XsdSchemaFactoryPool factoryPool;

  @Override
  protected Schema create() {
    SchemaFactory factory = null;
    try {
      factory = factoryPool.claim();
      return factory.newSchema(xsd);
    } catch (final SAXException cause) {
      throw new UftpConnectorException("Creating new schema instance failed for XSD: " + xsd, cause);
    } finally {
      factoryPool.release(factory);
    }
  }
}
