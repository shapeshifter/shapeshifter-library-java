// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.core.UftpTestSupport.assertException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.net.URL;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@ExtendWith(MockitoExtension.class)
class XsdSchemaPoolTest {

  private static final String XXE_ATTACK = "xml/xxe/FlexRequestResponse_with_XXE_Attack.xml";
  private static final String XXE_ATTACK_SSRF = "xml/xxe/FlexRequestResponse_with_XXE_Attack_SSRF.xml";

  @Mock
  private XsdSchemaFactoryPool factoryPool;
  @Mock
  private SchemaFactory factory;
  @Mock
  private Schema schema;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        factoryPool,
        factory,
        schema
    );
  }

  @Test
  void construction() throws Exception {
    var url = new URL("file:///test.xsd");
    final XsdSchemaPool testSubject = new XsdSchemaPool(url, factoryPool);

    assertThat(testSubject.getXsd()).isEqualTo(url);
    assertThat(testSubject.getFactoryPool()).isSameAs(factoryPool);
  }

  @Test
  void create() throws Exception {
    given(factoryPool.claim()).willReturn(factory);

    var url = new URL("file:///test.xsd");
    final XsdSchemaPool testSubject = new XsdSchemaPool(url, factoryPool);
    given(factory.newSchema(url)).willReturn(schema);

    assertThat(testSubject.create()).isSameAs(schema);

    verify(factoryPool).release(factory);
  }

  @Test
  void create_throws() throws Exception {
    given(factoryPool.claim()).willReturn(factory);

    var url = new URL("file:///test.xsd");
    final XsdSchemaPool testSubject = new XsdSchemaPool(url, factoryPool);

    var saxException = new SAXException("test");
    given(factory.newSchema(any(URL.class))).willThrow(saxException);

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, testSubject::create);

    assertException(actual, "Creating new schema instance failed for XSD: file:/test.xsd", saxException);

    verify(factoryPool).release(factory);
  }
  
  @Test
  void create_violate_xxe_then_fail() {
    doTestXXE(XXE_ATTACK, "DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true.");
  }

  @Test
  void create_violate_xxe_ssrf_then_fail() {
    doTestXXE(XXE_ATTACK_SSRF, "DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true.");
  }

  private void doTestXXE(String fileName, String errorMessage) {
    var xsdSchemaFactoryPool = new XsdSchemaFactoryPool();

    var url = this.getClass().getClassLoader().getResource(fileName);
    final XsdSchemaPool testSubject = new XsdSchemaPool(url, xsdSchemaFactoryPool);

    assertThatThrownBy((() ->
        testSubject.create()))
        .isInstanceOf(UftpConnectorException.class)
        .hasRootCauseInstanceOf(SAXParseException.class)
        .rootCause()
        .hasMessageContaining(errorMessage);
  }
}
