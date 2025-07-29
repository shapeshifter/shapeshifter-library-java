// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.sending;

/**
 * Exception thrown when an HTTP 5xx is received while sending an UFTP message.
 */
@SuppressWarnings("java:S110") // More than 5 parents useful and intended in this case
public final class UftpServerErrorException extends UftpSendException {

  public UftpServerErrorException(String message, int httpStatusCode) {
    super(message, httpStatusCode);
  }
  
}
