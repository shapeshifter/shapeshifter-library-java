package org.lfenergy.shapeshifter.connector.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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
    assertThat(testSubject.getHttpStatusCode()).isEqualTo(500);
  }

  @Test
  void testMessageAndCauseConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test", cause);
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isSameAs(cause);
    assertThat(testSubject.getHttpStatusCode()).isEqualTo(500);
  }

  @Test
  void testMessageAndHttpStatusConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test", HttpStatus.BAD_REQUEST);
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isNull();
    assertThat(testSubject.getHttpStatusCode()).isEqualTo(400);
  }

  @Test
  void testMessageAndHttpStatusCodeConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test", 123);
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isNull();
    assertThat(testSubject.getHttpStatusCode()).isEqualTo(123);
  }

  @Test
  void testMessageCauseAndHttpStatusConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test", cause, HttpStatus.BAD_REQUEST);
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isSameAs(cause);
    assertThat(testSubject.getHttpStatusCode()).isEqualTo(400);
  }

  @Test
  void testMessageCauseAndHttpStatusCodeConstruction() {
    UftpConnectorException testSubject = new UftpConnectorException("test", cause, 123);
    assertThat(testSubject.getMessage()).isEqualTo("test");
    assertThat(testSubject.getCause()).isSameAs(cause);
    assertThat(testSubject.getHttpStatusCode()).isEqualTo(123);
  }
}