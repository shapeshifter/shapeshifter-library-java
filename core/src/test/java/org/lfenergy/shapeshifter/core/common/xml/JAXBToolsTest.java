// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.core.UftpTestSupport.assertException;
import static org.lfenergy.shapeshifter.core.UftpTestSupport.assertExceptionCauseNotNull;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.util.JAXBResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.core.model.SigningDetails;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JAXBToolsTest {

  @InjectMocks
  private JAXBTools jaxbTools;

  @Test
  void getContext() {
    final JAXBContext ctx1 = jaxbTools.getJAXBContext(JAXBTools.class);
    final JAXBContext ctx2 = jaxbTools.getJAXBContext(UftpParticipant.class);
    final JAXBContext ctx3 = jaxbTools.getJAXBContext(UftpParticipant.class);

    assertThat(ctx1).isNotNull();
    assertThat(ctx2).isNotNull();
    assertThat(ctx3).isNotNull();
    assertThat(ctx2).isNotSameAs(ctx1);
    assertThat(ctx3).isNotSameAs(ctx1);
    assertThat(ctx3).isSameAs(ctx2);
  }

  @Test
  void getContextThrowsOnNull() {
    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> jaxbTools.getJAXBContext(null));

    assertException(actual, "Type to (de)serialize must be specified");
  }

  @Test
  void createMarshaller() {
    final Marshaller m1 = jaxbTools.createMarshaller(JAXBTools.class);
    final Marshaller m2 = jaxbTools.createMarshaller(UftpParticipant.class);
    final Marshaller m3 = jaxbTools.createMarshaller(UftpParticipant.class);

    assertThat(m1).isNotNull();
    assertThat(m2).isNotNull();
    assertThat(m3).isNotNull();
    assertThat(m2).isNotSameAs(m1);
    assertThat(m3).isNotSameAs(m1);
    assertThat(m3).isNotSameAs(m2);
  }

  @Test
  void createMarshallerThrows() {
    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> jaxbTools.createMarshaller(null));

    assertExceptionCauseNotNull(actual, "Failed to create JAXB Marshaller for class: null");
  }

  @Test
  void createUnmarshaller() {
    final Unmarshaller m1 = jaxbTools.createUnmarshaller(UftpParticipant.class);
    final Unmarshaller m2 = jaxbTools.createUnmarshaller(SigningDetails.class);
    final Unmarshaller m3 = jaxbTools.createUnmarshaller(SigningDetails.class);

    assertThat(m1).isNotNull();
    assertThat(m2).isNotNull();
    assertThat(m3).isNotNull();
    assertThat(m2).isNotSameAs(m1);
    assertThat(m3).isNotSameAs(m1);
    assertThat(m3).isNotSameAs(m2);
  }

  @Test
  void createUnmarshallerThrows() {
    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> jaxbTools.createUnmarshaller(null));

    assertExceptionCauseNotNull(actual, "Failed to create JAXB unmarshaller for class: null");
  }

  @Test
  void newJAXBResult() {
    final JAXBResult m1 = jaxbTools.newJAXBResult(UftpParticipant.class);
    final JAXBResult m2 = jaxbTools.newJAXBResult(SigningDetails.class);
    final JAXBResult m3 = jaxbTools.newJAXBResult(SigningDetails.class);

    assertThat(m1).isNotNull();
    assertThat(m2).isNotNull();
    assertThat(m3).isNotNull();
    assertThat(m2).isNotSameAs(m1);
    assertThat(m3).isNotSameAs(m1);
    assertThat(m3).isNotSameAs(m2);
  }

  @Test
  void newJAXBResultThrowsOnNull() {
    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> jaxbTools.newJAXBResult(null));

    assertExceptionCauseNotNull(actual, "Failed to create JAXB Result for class: null");
  }
}