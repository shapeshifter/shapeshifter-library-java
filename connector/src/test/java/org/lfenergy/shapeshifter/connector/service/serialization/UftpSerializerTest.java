// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.xsdinfo.UftpXsds;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.connector.common.xsd.XsdValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpSerializerTest {

  private static final String SIGNED_XML = "SIGNED_XML";
  private static final String FLEX_REQUEST_XML = "<FlexRequest></FlexRequest>";

  @Mock
  private XmlSerializer serializer;
  @Mock
  private XsdValidator xsdValidator;
  @InjectMocks
  private UftpSerializer testSubject;

  @Mock
  private SignedMessage signedMessage;
  @Mock
  private FlexRequest flexRequest;
  @Mock
  private IOException ioException;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        serializer,
        xsdValidator,
        signedMessage,
        flexRequest,
        ioException
    );
  }

  @Test
  void fromSignedXml() {
    given(serializer.fromXml(SIGNED_XML, SignedMessage.class)).willReturn(signedMessage);

    assertThat(testSubject.fromSignedXml(SIGNED_XML)).isEqualTo(signedMessage);

    verify(xsdValidator).validate(SIGNED_XML, UftpXsds.COMMON.getUrl());
  }

  @Test
  void fromSignedXml_throws() {
    var exception = new RuntimeException("test");
    given(serializer.fromXml(SIGNED_XML, SignedMessage.class)).willThrow(exception);

    UftpConnectorException thrown = assertThrows(UftpConnectorException.class, () ->
        testSubject.fromSignedXml(SIGNED_XML));

    assertException(thrown, "SignedMessage XML deserialization failed: test", exception, 400);
    verify(xsdValidator).validate(SIGNED_XML, UftpXsds.COMMON.getUrl());
  }

  @Test
  void fromPayloadXml() {
    given(serializer.fromXml(FLEX_REQUEST_XML, PayloadMessageType.class)).willReturn(flexRequest);

    assertThat(testSubject.fromPayloadXml(FLEX_REQUEST_XML)).isEqualTo(flexRequest);

    verify(xsdValidator).validate(FLEX_REQUEST_XML, UftpXsds.ALL.getUrl());
  }

  @Test
  void fromPayloadXml_throws() {
    var exception = new RuntimeException("test");
    given(serializer.fromXml(FLEX_REQUEST_XML, PayloadMessageType.class)).willThrow(exception);

    UftpConnectorException thrown = assertThrows(UftpConnectorException.class, () ->
        testSubject.fromPayloadXml(FLEX_REQUEST_XML));

    assertException(thrown, "Payload message XML deserialization failed: test", exception, 400);
    verify(xsdValidator).validate(FLEX_REQUEST_XML, UftpXsds.ALL.getUrl());
  }

  @Test
  void signedMessageToXml() {
    given(serializer.toXml(signedMessage)).willReturn(SIGNED_XML);

    assertThat(testSubject.toXml(signedMessage)).isEqualTo(SIGNED_XML);

    verify(xsdValidator).validate(SIGNED_XML, UftpXsds.COMMON.getUrl());
  }

  @Test
  void payloadMessageToXml() {
    given(serializer.toXml(flexRequest)).willReturn(FLEX_REQUEST_XML);

    assertThat(testSubject.toXml(flexRequest)).isEqualTo(FLEX_REQUEST_XML);

    verify(xsdValidator).validate(FLEX_REQUEST_XML, UftpXsds.ALL.getUrl());
  }

}
