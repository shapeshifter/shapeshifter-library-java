// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UftpMessageDirectionTest {

  @Test
  void inverse() {
    assertThat(UftpMessageDirection.INCOMING.inverse()).isEqualTo(UftpMessageDirection.OUTGOING);
    assertThat(UftpMessageDirection.OUTGOING.inverse()).isEqualTo(UftpMessageDirection.INCOMING);
  }
}