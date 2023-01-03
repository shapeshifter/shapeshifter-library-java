package org.lfenergy.shapeshifter.connector.service.forwarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpHandlerMethodTest {

  @Mock
  private Object bean;

  // XXX Used a know method instead of mocking one, because mocking Method is not allowed, see https://github.com/mockito/mockito/issues/2026
  private final Method receivingMethod = String.class.getMethods()[0];

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(bean);
  }

  @Test
  void construction() {
    var testSubject = new UftpHandlerMethod(bean, receivingMethod);

    assertThat(testSubject.bean()).isSameAs(bean);
    assertThat(testSubject.method()).isSameAs(receivingMethod);
  }
}
