// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UftpConnectorException extends RuntimeException {

  private final int httpStatusCode;

  public UftpConnectorException(String message) {
    this(message, null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public UftpConnectorException(String message, Throwable cause) {
    this(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public UftpConnectorException(String message, HttpStatus status) {
    this(message, null, status.value());
  }

  public UftpConnectorException(String message, int httpStatusCode) {
    this(message, null, httpStatusCode);
  }

  public UftpConnectorException(String message, Throwable cause, HttpStatus status) {
    this(message, cause, status.value());
  }

  public UftpConnectorException(String message, Throwable cause, int httpStatusCode) {
    super(message, cause);
    this.httpStatusCode = httpStatusCode;
  }
}
