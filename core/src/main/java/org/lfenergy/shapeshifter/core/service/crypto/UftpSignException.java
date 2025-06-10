// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.crypto;

/**
 * Thrown when message signing failed.
 */
public class UftpSignException extends UftpCryptoException {

  public UftpSignException(String message, Throwable cause) {
    super(message, cause);
  }

}
