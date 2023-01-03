package org.lfenergy.shapeshifter.connector.config;

import java.util.Optional;
import org.springframework.boot.context.properties.ConstructorBinding;

public record ValidationConfig(Boolean enabled) {

  @ConstructorBinding
  public ValidationConfig(Boolean enabled) {
    this.enabled = Optional.ofNullable(enabled).orElse(true);
  }
}
