// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation;

import org.lfenergy.shapeshifter.api.PayloadMessageType;

/**
 * Interface that can be implemented if you want to add additional message validation
 *
 * <pre>
 *
 * public class MyCustomValidator implements UftpUserDefinedValidator<FlexRequest> {
 *
 *   @Override
 *   public boolean appliesTo(Class<? extends FlexRequest> clazz) {
 *     // here you can filter on the message type that you want to validate on
 *     return clazz.equals(FlexRequest.class);
 *   }
 *
 *   @Override
 *   public boolean valid(UftpParticipant sender, FlexRequest flexRequest) {
 *     // implement your validation logic here; return true if valid, false otherwise
 *   }
 *
 *   @Override
 *   public String getReason() {
 *     // this is the reason of the validation failure, this is returned to the sender if validation fails
 *     return "My custom validation failed";
 *   }
 * }
 * </pre>
 *
 * @param <T> The type of UFTP payload message that can be validated by this class
 */
public interface UftpUserDefinedValidator<T extends PayloadMessageType> extends UftpValidator<T> {

  /**
   * Default order for user-defined validators. Normally these will be performed after the validations from the official specification.
   */
  @Override
  default int order() {
    return ValidationOrder.AFTER_SPEC;
  }
}
