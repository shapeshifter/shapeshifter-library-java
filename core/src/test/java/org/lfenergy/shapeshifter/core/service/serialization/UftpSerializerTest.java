// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.serialization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.api.xsdinfo.UftpXsds;
import org.lfenergy.shapeshifter.core.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.core.common.xsd.XsdValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

        assertThatThrownBy(() -> testSubject.fromSignedXml(SIGNED_XML))
                .isInstanceOf(UftpSerializerException.class)
                .hasMessage("SignedMessage XML deserialization failed: test");

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

        assertThatThrownBy(() ->
                testSubject.fromPayloadXml(FLEX_REQUEST_XML))
                .isInstanceOf(UftpSerializerException.class)
                .hasMessage("Payload message XML deserialization failed: test");

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
