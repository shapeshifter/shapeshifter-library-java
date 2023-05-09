// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.receiving;

/**
 * Thrown when a duplicate message is received.
 */
@SuppressWarnings("java:S110") // More than 5 parents is useful and intended in this case
public class DuplicateMessageException extends UftpReceiveException {

  DuplicateMessageException(String message) {
    super(message);
  }

}
