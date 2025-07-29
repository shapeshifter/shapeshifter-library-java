// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.crypto;

/**
 * Thrown when message verification failed.
 */
public class UftpVerifyException extends UftpCryptoException {

  public UftpVerifyException(String message) {
    super(message);
  }

  public UftpVerifyException(String message, Throwable cause) {
    super(message, cause);
  }

}
