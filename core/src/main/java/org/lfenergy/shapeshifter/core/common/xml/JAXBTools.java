// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.util.JAXBResult;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

public class JAXBTools {

  private static final Map<Class<?>, JAXBContext> JAXB_CONTEXTS = new ConcurrentHashMap<>();

  public JAXBContext getJAXBContext(final Class<?> type) {
      if (type == null) {
          throw new UftpConnectorException("Type to (de)serialize must be specified");
      }
      return JAXB_CONTEXTS.computeIfAbsent(type, key -> createJAXBContext(type));
  }

  private static JAXBContext createJAXBContext(Class<?> type) {
      try {
          return JAXBContext.newInstance(type);
      } catch (final Exception cause) {
          throw new UftpConnectorException("Failed to create JAXBContext for class: " + type, cause);
      }
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
