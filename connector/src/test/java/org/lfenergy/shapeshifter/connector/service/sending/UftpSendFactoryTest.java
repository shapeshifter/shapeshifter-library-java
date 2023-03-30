// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.sending;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpSendFactoryTest {

  private static final String ENDPOINT = "http://localhost:8080/uftp";
  private static final String CONTENT = "CONTENT";

  @InjectMocks
  private UftpSendFactory testSubject;

  @Test
  void newRestTemplate() {
    var v1 = testSubject.newRestTemplate();
    var v2 = testSubject.newRestTemplate();

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v1).isNotSameAs(v2);
  }
}