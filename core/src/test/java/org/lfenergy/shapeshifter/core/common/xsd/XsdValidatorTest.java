// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.core.UftpTestSupport.assertException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class XsdValidatorTest {

  @Mock
  private XsdSchemaProvider schemaProvider;
  @Mock
  private File xsdFile;
  @Mock
  private SchemaFactory schemaFactory;
  @Mock
  private Schema schema;
  @Mock
  private Validator validator;
  @InjectMocks
  private XsdValidator testSubject;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        schemaProvider,
        xsdFile,
        schemaFactory,
        schema,
        validator
    );
  }

  @Test
  void validateXsd() throws Exception {
    var url = new URL("file:///test.xsd");
    given(schemaProvider.getValidator(url)).willReturn(validator);
    testSubject.validate("xml", url);

    verify(validator).validate(any(Source.class));
  }

  @Test
  void validateXsdThrows() throws Exception {
    var url = new URL("file:///test.xsd");
    given(schemaProvider.getValidator(url)).willReturn(validator);

    var ioException = new IOException("test");
    doThrow(ioException).when(validator).validate(any(Source.class));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () ->
        testSubject.validate("xml", url));

    assertException(actual, "XSD validation failed: test", ioException, 400);
  }
}
