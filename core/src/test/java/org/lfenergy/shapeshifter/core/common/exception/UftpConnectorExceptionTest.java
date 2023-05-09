// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.core.common.HttpStatusCode;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpConnectorExceptionTest {

  @Mock
  private Throwable cause;

  @AfterEach
  void noMore() {
    Mockito.verifyNoMoreInteractions(cause);
  }

  @Test
  void testMessageConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test");
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isNull();
    assertThat(testSubject.getHttpStatusCode().getValue()).isEqualTo(500);
  }

  @Test
  void testMessageAndCauseConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test", cause);
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isSameAs(cause);
    assertThat(testSubject.getHttpStatusCode().getValue()).isEqualTo(500);
  }

  @Test
  void testMessageAndHttpStatusConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test", HttpStatusCode.BAD_REQUEST);
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isNull();
    assertThat(testSubject.getHttpStatusCode().getValue()).isEqualTo(400);
  }

  @Test
  void testMessageCauseAndHttpStatusConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test", HttpStatusCode.BAD_REQUEST, cause);
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isSameAs(cause);
    assertThat(testSubject.getHttpStatusCode().getValue()).isEqualTo(400);
  }

}