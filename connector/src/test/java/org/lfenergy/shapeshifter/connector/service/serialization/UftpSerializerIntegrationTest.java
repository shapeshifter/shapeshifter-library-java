package org.lfenergy.shapeshifter.connector.service.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lfenergy.shapeshifter.connector.common.xml.TestFileHelper.readXml;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
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

  @Test
  void flexRequest() throws Exception {
    String xml = TestFile.readResourceFileAsString(getClass(), "FlexRequest", ".xml");
    PayloadMessageType msg = serializer.fromPayloadXml(xml);
    String backToXml = serializer.toXml(msg);
    assertThat(msg).isNotNull();
    assertThat(backToXml).isNotNull();
    TestFile.compareAsXml(getClass(), "FlexRequest", backToXml);
  }

  @Test
  void flexRequestResponse() throws Exception {
    String xml = TestFile.readResourceFileAsString(getClass(), "FlexRequestResponse", ".xml");
    PayloadMessageType msg = serializer.fromPayloadXml(xml);
    String backToXml = serializer.toXml(msg);
    assertThat(msg).isNotNull();
    assertThat(backToXml).isNotNull();
    TestFile.compareAsXml(getClass(), "FlexRequestResponse", backToXml);
  }

  @Test
  void flexOffer() throws Exception {
    String xml = TestFile.readResourceFileAsString(getClass(), "FlexOffer", ".xml");
    PayloadMessageType msg = serializer.fromPayloadXml(xml);
    String backToXml = serializer.toXml(msg);
    assertThat(msg).isNotNull();
    assertThat(backToXml).isNotNull();
    TestFile.compareAsXml(getClass(), "FlexOffer", backToXml);
  }

  @Test
  void flexOfferResponse() throws Exception {
    String xml = TestFile.readResourceFileAsString(getClass(), "FlexOfferResponse", ".xml");
    PayloadMessageType msg = serializer.fromPayloadXml(xml);
    String backToXml = serializer.toXml(msg);
    assertThat(msg).isNotNull();
    assertThat(backToXml).isNotNull();
    TestFile.compareAsXml(getClass(), "FlexOfferResponse", backToXml);
  }

  @Test
  void flexOrder() throws Exception {
    String xml = TestFile.readResourceFileAsString(getClass(), "FlexOrder", ".xml");
    PayloadMessageType msg = serializer.fromPayloadXml(xml);
    String backToXml = serializer.toXml(msg);
    assertThat(msg).isNotNull();
    assertThat(backToXml).isNotNull();
    TestFile.compareAsXml(getClass(), "FlexOrder", backToXml);
  }

  @Test
  void flexOrderResponse() throws Exception {
    String xml = TestFile.readResourceFileAsString(getClass(), "FlexOrderResponse", ".xml");
    PayloadMessageType msg = serializer.fromPayloadXml(xml);
    String backToXml = serializer.toXml(msg);
    assertThat(msg).isNotNull();
    assertThat(backToXml).isNotNull();
    TestFile.compareAsXml(getClass(), "FlexOrderResponse", backToXml);
  }

  @Test
  void xxeAttackBlocked() {
    doTestXXE(XXE_ATTACK, "access is not allowed");
  }

  @Test
  void xxeAttackSsrfBlocked() {
    doTestXXE(XXE_ATTACK_SSRF, "access is not allowed");
  }

  private void doTestXXE(String fileName, String errorMessage) {
    var xml = readXml(fileName);
    assertThatThrownBy(() -> serializer.fromPayloadXml(xml))
        .isInstanceOf(UftpConnectorException.class)
        .hasRootCauseInstanceOf(SAXParseException.class)
        .getRootCause()
        .hasMessageContaining(errorMessage);
  }
}
