// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.model;

public record ValidationResult(boolean valid, String rejectionReason) {

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