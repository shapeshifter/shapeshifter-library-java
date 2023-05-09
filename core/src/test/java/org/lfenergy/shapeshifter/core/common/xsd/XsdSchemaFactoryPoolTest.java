// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.validation.SchemaFactory;
import org.junit.jupiter.api.Test;

class XsdSchemaFactoryPoolTest {

  @Test
  void claim_twice() {
    final XsdSchemaFactoryPool testSubject = new XsdSchemaFactoryPool();
    final SchemaFactory v1 = testSubject.claim();
    final SchemaFactory v2 = testSubject.claim();

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v2).isNotSameAs(v1);
  }

  @Test
  void claim_release_claim() {
    final XsdSchemaFactoryPool testSubject = new XsdSchemaFactoryPool();
    final SchemaFactory v1 = testSubject.claim();
    testSubject.release(v1);
    final SchemaFactory v2 = testSubject.claim();

    assertThat(v1).isNotNull();
    assertThat(v2).isNotNull();
    assertThat(v2).isSameAs(v1);
  }
}