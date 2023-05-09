// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.core.UftpTestSupport.assertExceptionCauseNotNull;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class XsdFactoryTest {

  @Mock
  private XsdSchemaFactoryPool factoryPool;

  @InjectMocks
  private XsdFactory testSubject;

  @Mock
  private IOException ioException;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(factoryPool, ioException);
  }

  @Test
  void newXsdSchemaPool() throws Exception {
    var url = new URL("file:///test.xsd");
    final XsdSchemaPool v1 = testSubject.newXsdSchemaPool(url);
    final XsdSchemaPool v2 = testSubject.newXsdSchemaPool(url);

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v2).isNotSameAs(v1);

    assertThat(v1.getXsd()).isEqualTo(url);
    assertThat(v1.getFactoryPool()).isSameAs(factoryPool);
  }

  @Test
  void fileContentToString() {
    final String content = testSubject.contentToString(
        new File("src/test/resources/input.txt")
    );
    assertThat(content).isEqualTo("BOE");
  }

  @Test
  void fileContentToString_throws() {
    var file = new File("not exist.file");
    
    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () ->
        testSubject.contentToString(file));

    assertExceptionCauseNotNull(actual, "Failed to read file content from 'not exist.file'.");
  }
}
