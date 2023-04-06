// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertException;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertExceptionCauseInstanceOf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.handler.testmapping.UftpTestCallableMapping;

class UftpPayloadHandlerTest {

  private UftpTestCallableMapping incomingCallableMapping;
  private UftpPayloadHandler testSubject;

  private final UftpParticipant from = new UftpParticipant("domain", USEFRoleType.DSO);
  private final FlexRequest flexRequest = new FlexRequest();
  private final FlexOffer flexOffer = new FlexOffer();
  private final FlexOrder flexOrder = new FlexOrder();

  @BeforeEach
  public void setup() throws Exception {
    incomingCallableMapping = new UftpTestCallableMapping();
    var outgoingCallableMapping = new UftpTestCallableMapping();

    var handlerMapping = new UftpHandlerMapping(List.of(incomingCallableMapping), List.of(outgoingCallableMapping));
    testSubject = new UftpPayloadHandler(handlerMapping);

    callDiscoverPostContructMethod(handlerMapping);
  }

  private void callDiscoverPostContructMethod(UftpHandlerMapping handlerMapping) throws Exception {
    Method method = UftpHandlerMapping.class.getDeclaredMethod("discover");
    method.setAccessible(true);
    method.invoke(handlerMapping);
  }

  @Test
  void handleFlexRequest() {
    testSubject.notifyNewIncomingMessage(from, flexRequest);

    assertThat(incomingCallableMapping.calledFlexOffer).isNull();
    assertThat(incomingCallableMapping.calledFlexRequest).isNotNull();
    assertThat(incomingCallableMapping.calledFlexRequest.sender()).isSameAs(from);
    assertThat(incomingCallableMapping.calledFlexRequest.message()).isSameAs(flexRequest);
  }

  @Test
  void handleFlexOfferThrowsWhenCalled() {
    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () ->
        testSubject.notifyNewIncomingMessage(from, flexOffer));

    assertThat(incomingCallableMapping.calledFlexRequest).isNull();
    assertThat(incomingCallableMapping.calledFlexOffer).isNotNull();
    assertThat(incomingCallableMapping.calledFlexOffer.sender()).isSameAs(from);
    assertThat(incomingCallableMapping.calledFlexOffer.message()).isSameAs(flexOffer);

    assertExceptionCauseInstanceOf(actual, "Exception during processing of message of type: " + FlexOffer.class.getSimpleName(),
                                   InvocationTargetException.class);
    assertThat(actual.getCause().getCause()).isInstanceOf(UftpConnectorException.class);
    UftpConnectorException rootCause = (UftpConnectorException) actual.getCause().getCause();
    assertException(rootCause, "thrown from onFlexOffer");
  }

  @Test
  void handleUnmappedType() {
    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () ->
        testSubject.notifyNewIncomingMessage(from, flexOrder));

    assertThat(incomingCallableMapping.calledFlexRequest).isNull();
    assertThat(incomingCallableMapping.calledFlexOffer).isNull();

    assertException(actual, "No incoming handler method found for message type: " + FlexOrder.class.getName());
  }
}