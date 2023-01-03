package org.lfenergy.shapeshifter.connector.service.validation.model;

import static org.apache.commons.lang3.StringUtils.isBlank;

public record ValidationResult(boolean valid, String rejectionReason) {

  public static ValidationResult ok() {
    return new ValidationResult(true, null);
  }

  public static ValidationResult rejection(String reason) {
    if (isBlank(reason)) {
      throw new IllegalArgumentException("Rejection reason cannot be blank.");
    }
    return new ValidationResult(false, reason);
  }
}