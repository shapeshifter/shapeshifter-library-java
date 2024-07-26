// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Pre-defined constants to indicate the order with {@link UftpValidator#order}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationOrder {

  /**
   * Must be performed BEFORE the validations from the official specification.
   */
  public static final int BEFORE_SPEC = 0;

  /**
   * Base validations from the official specification such as: Version, TimeStamp, Sender, Recipient.
   */
  public static final int SPEC_BASE = BEFORE_SPEC + 100;

  /**
   * Generic Flex message validations from the official specification that must be performed after base validations, such as: TimeZone, Period, ExpirationDateTime, ISPs, etc.
   */
  public static final int AFTER_SPEC_BASE = SPEC_BASE + 100;

  /**
   * Generic Flex message validations from the official specification that must be performed after base validations, such as: TimeZone, Period, ExpirationDateTime, ISPs, etc.
   */
  public static final int SPEC_FLEX_MESSAGE = AFTER_SPEC_BASE + 100;

  /**
   * Message-specific validations from the official specification that must be performed after base and generic Flex message validations have been performed, such as: referenced
   * message IDs, ContractID, CongestionPoint, etc.
   */
  public static final int SPEC_MESSAGE_SPECIFIC = SPEC_FLEX_MESSAGE + 100;

  /**
   * May be performed AFTER the validations from the official specification.
   *
   * <p>This is also the default for user-defined validations.</p>
   */
  public static final int AFTER_SPEC = SPEC_MESSAGE_SPECIFIC + 100;

}
