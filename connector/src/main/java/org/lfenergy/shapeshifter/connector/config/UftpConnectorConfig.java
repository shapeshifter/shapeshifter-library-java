// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.config;

import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("uftp-connector")
public record UftpConnectorConfig(ReceivingConfig receiving) {

  @ConstructorBinding
  public UftpConnectorConfig(ReceivingConfig receiving) {
    this.receiving = Optional.ofNullable(receiving).orElseGet(() -> new ReceivingConfig(null));
  }
}
