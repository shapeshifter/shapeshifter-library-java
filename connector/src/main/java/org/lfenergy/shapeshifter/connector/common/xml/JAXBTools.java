package org.lfenergy.shapeshifter.connector.common.xml;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.springframework.stereotype.Component;

@Component
public class JAXBTools {

  private static Map<Class<?>, JAXBContext> jaxbContexts = new HashMap<>();

  public JAXBContext getJAXBContext(final Class<?> type) {
    if (!jaxbContexts.containsKey(type)) {
      try {
        jaxbContexts.put(type, JAXBContext.newInstance(type));
      } catch (final Exception cause) {
        throw new UftpConnectorException("Failed to create JAXBContext for class: " + type, cause);
      }
    }
    return jaxbContexts.get(type);
  }

  public Marshaller createMarshaller(final Class<?> type) {
    try {
      return getJAXBContext(type).createMarshaller();
    } catch (final Exception cause) {
      throw new UftpConnectorException("Failed to create JAXB Marshaller for class: " + type, cause);
    }
  }

  public Unmarshaller createUnmarshaller(final Class<?> type) {
    try {
      return getJAXBContext(type).createUnmarshaller();
    } catch (final Exception cause) {
      throw new UftpConnectorException("Failed to create JAXB unmarshaller for class: " + type, cause);
    }
  }

  public JAXBResult newJAXBResult(final Class<?> type) {
    try {
      return new JAXBResult(getJAXBContext(type));
    } catch (final Exception cause) {
      throw new UftpConnectorException("Failed to create JAXB Result for class: " + type, cause);
    }
  }

}
