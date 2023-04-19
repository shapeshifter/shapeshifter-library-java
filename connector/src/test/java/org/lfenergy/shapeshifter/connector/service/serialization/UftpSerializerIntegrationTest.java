// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lfenergy.shapeshifter.connector.common.xml.TestFileHelper.readXml;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferResponse;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderResponse;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.application.TestSpringConfig;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXParseException;

@Slf4j
@SpringBootTest(classes = TestSpringConfig.class)
class UftpSerializerIntegrationTest {

  private static final String XXE_ATTACK = "xml/xxe/FlexRequestResponse_with_XXE_Attack.xml";
  private static final String XXE_ATTACK_SSRF = "xml/xxe/FlexRequestResponse_with_XXE_Attack_SSRF.xml";
  private static final String XML_PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

  @Autowired
  private UftpSerializer serializer;

  @Test
  void signedMessage() throws Exception {
    var xml = TestFile.readResourceFileAsString(getClass(), "SignedMessage", ".xml");

    testSignedMessage(xml);
    testSignedMessage(XML_PROLOG + xml);
  }

  private void testSignedMessage(String xml) throws IOException {
    var msg = serializer.fromSignedXml(xml);
    var backToXml = serializer.toXml(msg);
    assertThat(msg).isNotNull();
    assertThat(backToXml).isNotNull();
    TestFile.compareAsXml(getClass(), "SignedMessage", backToXml);
  }

  @ParameterizedTest
  @ValueSource(classes = {
      FlexRequest.class,
      FlexRequestResponse.class,
      FlexOffer.class,
      FlexOfferResponse.class,
      FlexOrder.class,
      FlexOrderResponse.class
  })
  void payloadMessage(Class<? extends PayloadMessageType> payloadMessageType) throws IOException {
    var testName = payloadMessageType.getSimpleName();
    var xml = TestFile.readResourceFileAsString(getClass(), testName, ".xml");

    testPayloadMessage(testName, xml);
    testPayloadMessage(testName, XML_PROLOG + xml);
  }

  private void testPayloadMessage(String testName, String xml) throws IOException {
    var msg = serializer.fromPayloadXml(xml);
    assertThat(msg).isNotNull();

    var backToXml = serializer.toXml(msg);
    assertThat(backToXml).isNotNull();
    TestFile.compareAsXml(getClass(), testName, backToXml);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      XXE_ATTACK,
      XXE_ATTACK_SSRF
  })
  void xxe(String fileName) {
    var xml = readXml(fileName);
    assertThatThrownBy(() -> serializer.fromPayloadXml(xml))
        .isInstanceOf(UftpConnectorException.class)
        .hasRootCauseInstanceOf(SAXParseException.class)
        .rootCause()
        .hasMessageContaining("access is not allowed");
  }
}
