// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lfenergy.shapeshifter.core.common.xml.TestFileHelper.readXml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.xml.sax.SAXParseException;

class XmlSerializerTest {

  private static final String SIGNED_MESSAGE_XML = "<SignedMessage></SignedMessage>";
  private static final String FLEX_REQUEST_XML = "<FlexRequest></FlexRequest>";
  private static final String XXE_ATTACK = "xml/xxe/FlexRequestResponse_with_XXE_Attack.xml";
  private static final String XXE_ATTACK_SSRF = "xml/xxe/FlexRequestResponse_with_XXE_Attack_SSRF.xml";
  private static final String XML_PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

  private final XmlSerializer xmlSerializer = new XmlSerializer();

  @Test
  void fromXml_SignedMessage() {
    var signedMessage = xmlSerializer.fromXml(SIGNED_MESSAGE_XML, SignedMessage.class);

    assertThat(signedMessage).isNotNull();
  }

  @Test
  void fromXml_SignedMessage_with_prolog() {
    var signedMessage = xmlSerializer.fromXml(XML_PROLOG + SIGNED_MESSAGE_XML, SignedMessage.class);

    assertThat(signedMessage).isNotNull();
  }

  @Test
  void fromXml_PayloadMessage() {
    var flexRequest = xmlSerializer.fromXml(FLEX_REQUEST_XML, FlexRequest.class);

    assertThat(flexRequest).isNotNull();
  }

  @Test
  void fromXml_PayloadMessage_with_prolog() {
    var flexRequest = xmlSerializer.fromXml(XML_PROLOG + FLEX_REQUEST_XML, FlexRequest.class);

    assertThat(flexRequest).isNotNull();
  }

  @Test
  void toXml_SignedMessage() {
    var xml = xmlSerializer.toXml(new SignedMessage());

    assertThat(xml).isEqualTo(XML_PROLOG + "<SignedMessage/>");
  }

  @Test
  void toXml_PayloadMessage() {
    var xml = xmlSerializer.toXml(new FlexRequest());

    assertThat(xml).isEqualTo(XML_PROLOG + "<FlexRequest Revision=\"0\"/>");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      XXE_ATTACK,
      XXE_ATTACK_SSRF
  })
  void xxeAttacksBlocked(String xml) {
    var xml1 = readXml(xml);
    assertThatThrownBy(() -> xmlSerializer.fromXml(xml1, PayloadMessageType.class))
        .isInstanceOf(UftpConnectorException.class)
        .hasRootCauseInstanceOf(SAXParseException.class)
        .hasRootCauseMessage("DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true.");
  }

}