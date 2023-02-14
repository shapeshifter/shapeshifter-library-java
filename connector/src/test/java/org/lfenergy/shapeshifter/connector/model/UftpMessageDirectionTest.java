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