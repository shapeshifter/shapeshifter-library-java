package org.lfenergy.shapeshifter.connector.config;

import java.util.Optional;
import org.springframework.boot.context.properties.ConstructorBinding;

public record ReceivingConfig(ValidationConfig validation) {

  @ConstructorBinding
  public ReceivingConfig(ValidationConfig validation) {
    this.validation = Optional.ofNullable(validation).orElseGet(() -> new ValidationConfig(true));
  }
}
