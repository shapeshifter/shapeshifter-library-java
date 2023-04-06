// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.USEFRoleType;

class UftpMessageTest {

  private static final String DSO_DOMAIN = "DSO_DOMAIN";
  private static final String AGR_DOMAIN = "AGR_DOMAIN";
  private static final String FLEX_REQUEST_MESSAGE_ID = "FLEX_REQUEST_MESSAGE_ID";

  @Test
  void createIncoming() {
    var sender = new UftpParticipant(DSO_DOMAIN, USEFRoleType.DSO);
    var payloadMessage = new FlexRequest();

    var uftpMessage = UftpMessage.createIncoming(sender, payloadMessage);

    assertThat(uftpMessage.direction()).isEqualTo(UftpMessageDirection.INCOMING);
    assertThat(uftpMessage.payloadMessage()).isSameAs(payloadMessage);
    assertThat(uftpMessage.sender()).isSameAs(sender);
  }

  @Test
  void createOutgoing() {
    var sender = new UftpParticipant(DSO_DOMAIN, USEFRoleType.DSO);
    var payloadMessage = new FlexRequest();

    var uftpMessage = UftpMessage.createOutgoing(sender, payloadMessage);

    assertThat(uftpMessage.direction()).isEqualTo(UftpMessageDirection.OUTGOING);
    assertThat(uftpMessage.payloadMessage()).isSameAs(payloadMessage);
    assertThat(uftpMessage.sender()).isSameAs(sender);
  }

  @Test
  void referenceToPreviousMessage() {
    var sender = new UftpParticipant(AGR_DOMAIN, USEFRoleType.AGR);

    var payloadMessage = new FlexOffer();
    payloadMessage.setSenderDomain(AGR_DOMAIN);
    payloadMessage.setRecipientDomain(DSO_DOMAIN);

    var uftpMessage = UftpMessage.createOutgoing(sender, payloadMessage);

    var referenceToPreviousMessage = uftpMessage.referenceToPreviousMessage(FLEX_REQUEST_MESSAGE_ID, FlexRequest.class);

    assertThat(referenceToPreviousMessage.messageID()).isEqualTo(FLEX_REQUEST_MESSAGE_ID);
    assertThat(referenceToPreviousMessage.senderDomain()).isEqualTo(DSO_DOMAIN);
    assertThat(referenceToPreviousMessage.recipientDomain()).isEqualTo(AGR_DOMAIN);
    assertThat(referenceToPreviousMessage.direction()).isEqualTo(UftpMessageDirection.INCOMING);
    assertThat(referenceToPreviousMessage.type()).isEqualTo(FlexRequest.class);
  }
}