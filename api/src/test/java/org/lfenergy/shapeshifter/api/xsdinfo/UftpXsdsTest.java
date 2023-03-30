// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.api.xsdinfo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UftpXsdsTest {

  @Test
  void resourcesAreFoundOnClassPath() {
    assertThat(UftpXsds.values()).allSatisfy(value -> assertThat(value.getUrl()).isNotNull());
  }
}
