// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.service.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.model.OutgoingUftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UftpPayloadDispatcherTest {

  public static final String SIGNED_MESSAGE_XML = "<SignedMessage/>";
  public static final String PAYLOAD_MESSAGE_XML = "<FlexRequest/>";
  @Mock
  private UftpIncomingHandler<PayloadMessageType> incomingHandler;

  @Mock
  private UftpOutgoingHandler<PayloadMessageType> outgoingHandler;

  private UftpPayloadDispatcher testSubject;

  @BeforeEach
  void setUp() {
    testSubject = new UftpPayloadDispatcher(
        List.of(incomingHandler),
        List.of(outgoingHandler)
    );
  }

  @Test
  void notifyNewIncomingMessage() {
    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    given(incomingHandler.isSupported(FlexRequest.class)).willReturn(true);

    var incomingUftpMessage = IncomingUftpMessage.<PayloadMessageType>create(sender, message, SIGNED_MESSAGE_XML, PAYLOAD_MESSAGE_XML);
    testSubject.notifyNewIncomingMessage(incomingUftpMessage);

    verify(incomingHandler).isSupported(FlexRequest.class);
    verify(incomingHandler).handle(incomingUftpMessage);
  }

  @Test
  void notifyNewIncomingMessage_noSupportedHandler() {
    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    var incomingUftpMessage = IncomingUftpMessage.<PayloadMessageType>create(sender, message, SIGNED_MESSAGE_XML, PAYLOAD_MESSAGE_XML);
    assertThatThrownBy(() -> testSubject.notifyNewIncomingMessage(incomingUftpMessage))
        .isInstanceOf(UftpConnectorException.class)
        .hasMessage("No incoming handler for message type: FlexRequest");

    verify(incomingHandler).isSupported(FlexRequest.class);
    verifyNoMoreInteractions(incomingHandler);
  }

  @Test
  void notifyNewIncomingMessage_multipleSupportedHandlers() {
    var incomingHandler2 = (UftpIncomingHandler<PayloadMessageType>) mock(UftpIncomingHandler.class);

    testSubject = new UftpPayloadDispatcher(
        List.of(incomingHandler, incomingHandler2),
        List.of(outgoingHandler)
    );

    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    given(incomingHandler.isSupported(FlexRequest.class)).willReturn(true);
    given(incomingHandler2.isSupported(FlexRequest.class)).willReturn(true);

    var incomingUftpMessage = IncomingUftpMessage.<PayloadMessageType>create(sender, message, SIGNED_MESSAGE_XML, PAYLOAD_MESSAGE_XML);
    testSubject.notifyNewIncomingMessage(incomingUftpMessage);

    verify(incomingHandler).isSupported(FlexRequest.class);
    verify(incomingHandler).handle(incomingUftpMessage);

    verify(incomingHandler2).isSupported(FlexRequest.class);
    verify(incomingHandler2).handle(incomingUftpMessage);
  }

  @Test
  void notifyNewOutgoingMessage() {
    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    OutgoingUftpMessage<PayloadMessageType> outgoingUftpMessage = OutgoingUftpMessage.create(sender, message);
    given(outgoingHandler.isSupported(FlexRequest.class)).willReturn(true);

    testSubject.notifyNewOutgoingMessage(outgoingUftpMessage);

    verify(outgoingHandler).isSupported(FlexRequest.class);
    verify(outgoingHandler).handle(outgoingUftpMessage);
  }

  @Test
  void notifyNewOutgoingMessage_noSupportedHandler() {
    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    var outgoingUftpMessage = OutgoingUftpMessage.create(sender, message);
    assertThatThrownBy(() -> testSubject.notifyNewOutgoingMessage(outgoingUftpMessage))
        .isInstanceOf(UftpConnectorException.class)
        .hasMessage("No outgoing handler for message type: FlexRequest");

    verify(outgoingHandler).isSupported(FlexRequest.class);
    verifyNoMoreInteractions(outgoingHandler);
  }

  @Test
  void notifyNewOutgoingMessage_multipleSupportedHandlers() {
    var outgoingHandler2 = (UftpOutgoingHandler<PayloadMessageType>) mock(UftpOutgoingHandler.class);

    testSubject = new UftpPayloadDispatcher(
        List.of(incomingHandler),
        List.of(outgoingHandler, outgoingHandler2)
    );

    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    given(outgoingHandler.isSupported(FlexRequest.class)).willReturn(true);
    given(outgoingHandler2.isSupported(FlexRequest.class)).willReturn(true);

    OutgoingUftpMessage<PayloadMessageType> outgoingUftpMessage = OutgoingUftpMessage.create(sender, message);
    testSubject.notifyNewOutgoingMessage(outgoingUftpMessage);

    verify(outgoingHandler).isSupported(FlexRequest.class);
    verify(outgoingHandler).handle(outgoingUftpMessage);

    verify(outgoingHandler2).isSupported(FlexRequest.class);
    verify(outgoingHandler2).handle(outgoingUftpMessage);
  }

}