// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.config;

import java.util.Optional;
import org.springframework.boot.context.properties.ConstructorBinding;

public record ReceivingConfig(ValidationConfig validation) {

  @ConstructorBinding
  public ReceivingConfig(ValidationConfig validation) {
    this.validation = Optional.ofNullable(validation).orElseGet(() -> new ValidationConfig(true));
  }
}
