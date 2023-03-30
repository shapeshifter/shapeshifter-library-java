// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.common.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.xml.transform.StringSource;

@ExtendWith(MockitoExtension.class)
class XmlFactoryTest {

  public static final String STRING_CONTENT = "STRING_CONTENT";

  @InjectMocks
  private XmlFactory testSubject;

  @Test
  void testNewStringWriter() {
    StringWriter v1 = testSubject.newStringWriter();
    StringWriter v2 = testSubject.newStringWriter();

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v1).isNotSameAs(v2);
  }

  @Test
  void newStringSource() {
    StringSource v1 = testSubject.newStringSource(STRING_CONTENT);
    StringSource v2 = testSubject.newStringSource(STRING_CONTENT);

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v1).isNotSameAs(v2);

    assertThat(v1.toString()).isEqualTo(STRING_CONTENT);
  }
}