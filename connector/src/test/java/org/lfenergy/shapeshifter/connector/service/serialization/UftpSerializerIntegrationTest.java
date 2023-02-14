package org.lfenergy.shapeshifter.connector.service.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lfenergy.shapeshifter.connector.common.xml.TestFileHelper.readXml;

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
import org.lfenergy.shapeshifter.api.SignedMessage;
import org.lfenergy.shapeshifter.connector.application.TestSpringConfigExcludingTestMappings;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXParseException;

@Slf4j
@SpringBootTest(classes = TestSpringConfigExcludingTestMappings.class)
class UftpSerializerIntegrationTest {

  private static final String XXE_ATTACK = "xml/xxe/FlexRequestResponse_with_XXE_Attack.xml";
  private static final String XXE_ATTACK_SSRF = "xml/xxe/FlexRequestResponse_with_XXE_Attack_SSRF.xml";

  @Autowired
  private UftpSerializer serializer;

  @Test
  void signedMessage() throws Exception {
    String xml = TestFile.readResourceFileAsString(getClass(), "SignedMessage", ".xml");
    SignedMessage msg = serializer.fromSignedXml(xml);
    String backToXml = serializer.toXml(msg);
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
  void payloadMessage(Class<? extends PayloadMessageType> payloadMessageType) throws Exception {
    var testName = payloadMessageType.getSimpleName();

    var xml = TestFile.readResourceFileAsString(getClass(), testName, ".xml");

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
