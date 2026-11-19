// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.model;

public record ValidationResult(boolean valid, String rejectionReason) {

  /**
   * Creates an ok result when valid, otherwise a rejection with the given reason.
   *
   * @param valid           whether the validation succeeded
   * @param rejectionReason the reason of the rejection; only used when {@code valid} is false
   * @throws IllegalArgumentException if {@code valid} is false and the reason is null or blank
   */
  public static ValidationResult of(boolean valid, String rejectionReason) {
    return valid ? ValidationResult.ok() : ValidationResult.rejection(rejectionReason);
  }

  public static ValidationResult ok() {
    return new ValidationResult(true, null);
  }

  public static ValidationResult rejection(String reason) {
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("Rejection reason cannot be blank.");
    }
    return new ValidationResult(false, reason);
  }
}