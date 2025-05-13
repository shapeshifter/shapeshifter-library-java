// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.sending;

import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

import java.util.Optional;

/**
 * Base class for exceptions thrown when sending an UFTP message fails e.g. when a request fails because of an error response, or a low level I/O error.
 */
public class UftpSendException extends UftpConnectorException {

  private final Integer httpStatusCode;

  public UftpSendException(String message) {
    super(message);
    this.httpStatusCode = null;
  }

  public UftpSendException(String message, Throwable cause) {
    super(message, cause);
    this.httpStatusCode = null;
  }

  public UftpSendException(String message, int httpStatusCode) {
    super(message);
    this.httpStatusCode = httpStatusCode;
  }

  public Optional<Integer> getHttpStatusCode() {
    return Optional.ofNullable(httpStatusCode);
  }
}
