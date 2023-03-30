// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UftpConnectorConfigTest {

  @Test
  void construction_null() {
    var config = new UftpConnectorConfig(null);

    assertThat(config.receiving()).isNotNull();
  }

  @Test
  void construction_instance() {
    var recieving = new ReceivingConfig(null);
    var config = new UftpConnectorConfig(recieving);

    assertThat(config.receiving()).isSameAs(recieving);
  }

}