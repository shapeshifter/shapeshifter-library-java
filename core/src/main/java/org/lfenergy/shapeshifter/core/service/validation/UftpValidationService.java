// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

/**
 * Service to validate UFTP messages.
 */
@CommonsLog
public final class UftpValidationService {

  private final List<UftpValidator<? extends PayloadMessageType>> validators;

  /**
   * Initializes the service with a discovered list of {@link UftpValidator} beans.
   *
   * @param validators discovered beans that implement {@link UftpValidator}. No discovery order is assumed as the validators will be sorted by their defined
   * {@link UftpValidator#order}.
   */
  public UftpValidationService(@NonNull Collection<UftpValidator<? extends PayloadMessageType>> validators) {
    this.validators = validators.stream()
                                .sorted(Comparator.<UftpValidator<?>>comparingInt(UftpValidator::order)
                                                  // In case two validators have the same order, sort by name to stay deterministic across restarts
                                                  .thenComparing(validator -> validator.getClass().getSimpleName()))
                                .toList();
  }

  public ValidationResult validate(UftpMessage<? extends PayloadMessageType> uftpMessage) {
    log.debug(String.format("Validating received %s message", uftpMessage.payloadMessage().getClass()));
    return validators.stream()
                     .map(validator -> validate(validator, uftpMessage))
                     .filter(validationResult -> !validationResult.valid())
                     .findFirst()
                     .orElseGet(ValidationResult::ok);
  }

  @SuppressWarnings("unchecked")
  // There is an unchecked cast to type T. However, this has already been verified by the appliesTo method of the validator.
  private <T extends PayloadMessageType> ValidationResult validate(UftpValidator<T> validator, UftpMessage<? extends PayloadMessageType> uftpMessage) {
    if (validator.appliesTo(uftpMessage.payloadMessage().getClass()) && !validator.isValid((UftpMessage<T>) uftpMessage)) {
      return ValidationResult.rejection(validator.getReason());
    }
    return ValidationResult.ok();
  }
}