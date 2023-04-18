// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UftpPayloadHandlerTest {

  @Mock
  private UftpIncomingHandler<PayloadMessageType> incomingHandler;

  @Mock
  private UftpOutgoingHandler<PayloadMessageType> outgoingHandler;

  private UftpPayloadHandler testSubject;

  @BeforeEach
  void setUp() {
    testSubject = new UftpPayloadHandler(
        List.of(incomingHandler),
        List.of(outgoingHandler)
    );
  }

  @Test
  void notifyNewIncomingMessage() {
    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    given(incomingHandler.isSupported(FlexRequest.class)).willReturn(true);

    testSubject.notifyNewIncomingMessage(sender, message);

    verify(incomingHandler).isSupported(FlexRequest.class);
    verify(incomingHandler).handle(sender, message);
  }

  @Test
  void notifyNewIncomingMessage_noSupportedHandler() {
    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    assertThatThrownBy(() -> testSubject.notifyNewIncomingMessage(sender, message))
        .isInstanceOf(UftpConnectorException.class)
        .hasMessage("No incoming handler for message type: FlexRequest");

    verify(incomingHandler).isSupported(FlexRequest.class);
    verifyNoMoreInteractions(incomingHandler);
  }

  @Test
  void notifyNewIncomingMessage_multipleSupportedHandlers() {
    var incomingHandler2 = (UftpIncomingHandler<PayloadMessageType>) mock(UftpIncomingHandler.class);

    testSubject = new UftpPayloadHandler(
        List.of(incomingHandler, incomingHandler2),
        List.of(outgoingHandler)
    );

    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    given(incomingHandler.isSupported(FlexRequest.class)).willReturn(true);
    given(incomingHandler2.isSupported(FlexRequest.class)).willReturn(true);

    testSubject.notifyNewIncomingMessage(sender, message);

    verify(incomingHandler).isSupported(FlexRequest.class);
    verify(incomingHandler).handle(sender, message);

    verify(incomingHandler2).isSupported(FlexRequest.class);
    verify(incomingHandler2).handle(sender, message);
  }

  @Test
  void notifyNewOutgoingMessage() {
    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    given(outgoingHandler.isSupported(FlexRequest.class)).willReturn(true);

    testSubject.notifyNewOutgoingMessage(sender, message);

    verify(outgoingHandler).isSupported(FlexRequest.class);
    verify(outgoingHandler).handle(sender, message);
  }

  @Test
  void notifyNewOutgoingMessage_noSupportedHandler() {
    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    assertThatThrownBy(() -> testSubject.notifyNewOutgoingMessage(sender, message))
        .isInstanceOf(UftpConnectorException.class)
        .hasMessage("No outgoing handler for message type: FlexRequest");

    verify(outgoingHandler).isSupported(FlexRequest.class);
    verifyNoMoreInteractions(outgoingHandler);
  }

  @Test
  void notifyNewOutgoingMessage_multipleSupportedHandlers() {
    var outgoingHandler2 = (UftpOutgoingHandler<PayloadMessageType>) mock(UftpOutgoingHandler.class);

    testSubject = new UftpPayloadHandler(
        List.of(incomingHandler),
        List.of(outgoingHandler, outgoingHandler2)
    );

    var sender = new UftpParticipant("domain", USEFRoleType.DSO);
    var message = new FlexRequest();

    given(outgoingHandler.isSupported(FlexRequest.class)).willReturn(true);
    given(outgoingHandler2.isSupported(FlexRequest.class)).willReturn(true);

    testSubject.notifyNewOutgoingMessage(sender, message);

    verify(outgoingHandler).isSupported(FlexRequest.class);
    verify(outgoingHandler).handle(sender, message);

    verify(outgoingHandler2).isSupported(FlexRequest.class);
    verify(outgoingHandler2).handle(sender, message);
  }

}