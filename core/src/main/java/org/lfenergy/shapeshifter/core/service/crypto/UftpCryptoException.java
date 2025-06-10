// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.crypto;

import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

/**
 * Thrown when message verification failed.
 */
@SuppressWarnings("java:S110") // More than 5 parents is useful and intended in this case
public class UftpCryptoException extends UftpConnectorException {

  public UftpCryptoException(String message) {
    super(message);
  }

  public UftpCryptoException(String message, Throwable cause) {
    super(message, cause);
  }

}
