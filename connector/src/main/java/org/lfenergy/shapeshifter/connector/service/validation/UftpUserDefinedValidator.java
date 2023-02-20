package org.lfenergy.shapeshifter.connector.service.validation;

import org.lfenergy.shapeshifter.api.PayloadMessageType;

public interface UftpUserDefinedValidator<T extends PayloadMessageType> extends UftpValidator<T> {

  /**
   * Default order for user-defined validators. Normally these will be performed after the validations from the official specification.
   */
  @Override
  default int order() {
    return ValidationOrder.AFTER_SPEC;
  }
}
