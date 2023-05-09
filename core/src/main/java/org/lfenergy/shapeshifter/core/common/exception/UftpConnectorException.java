// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.exception;

import lombok.Getter;
import org.lfenergy.shapeshifter.core.common.HttpStatusCode;

@Getter
public class UftpConnectorException extends RuntimeException {

  private final HttpStatusCode httpStatusCode;

  public UftpConnectorException(String message) {
    this(message, HttpStatusCode.INTERNAL_SERVER_ERROR, null);
  }

  public UftpConnectorException(String message, Throwable cause) {
    this(message, HttpStatusCode.INTERNAL_SERVER_ERROR, cause);
  }

  public UftpConnectorException(String message, HttpStatusCode httpStatusCode) {
    this(message, httpStatusCode, null);
  }

  public UftpConnectorException(String message, HttpStatusCode httpStatusCode, Throwable cause) {
    super(message, cause);
    this.httpStatusCode = httpStatusCode;
  }
}
