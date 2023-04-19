// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.connector.application.TestSpringConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestSpringConfig.class)
class UftpConnectorConfigSpringBootTest {

  @Autowired
  private UftpConnectorConfig config;

  @Test
  void test() {
    assertThat(config.receiving().validation().enabled()).isTrue();
  }

}