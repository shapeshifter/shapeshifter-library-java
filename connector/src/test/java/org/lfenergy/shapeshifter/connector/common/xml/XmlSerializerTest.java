package org.lfenergy.shapeshifter.connector.common.xml;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lfenergy.shapeshifter.connector.common.xml.TestFileHelper.readXml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXParseException;

@ExtendWith(MockitoExtension.class)
class XmlSerializerTest {

  private static final String XXE_ATTACK = "xml/xxe/FlexRequestResponse_with_XXE_Attack.xml";
  private static final String XXE_ATTACK_SSRF = "xml/xxe/FlexRequestResponse_with_XXE_Attack_SSRF.xml";
  @Mock
  private XmlFactory factory;
  @InjectMocks
  private XmlSerializer testSubject;

  @BeforeEach
  void setup() {
    this.testSubject = new XmlSerializer(new JAXBTools(), factory);
  }

  @Test
  void xxeAttackBlocked() {
    doTestXXE(XXE_ATTACK, "DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true.");
  }

  @Test
  void xxeAttackSsrfBlocked() {
    doTestXXE(XXE_ATTACK_SSRF, "DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true.");
  }

  private void doTestXXE(String fileName, String errorMessage) {
    var xml = readXml(fileName);
    assertThatThrownBy(() -> testSubject.fromXml(xml, PayloadMessageType.class))
        .isInstanceOf(UftpConnectorException.class)
        .hasRootCauseInstanceOf(SAXParseException.class)
        .getRootCause()
        .hasMessageContaining(errorMessage);
  }
}