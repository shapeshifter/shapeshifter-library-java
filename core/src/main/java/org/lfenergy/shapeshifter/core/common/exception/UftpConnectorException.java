// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.exception;

import lombok.Getter;

@Getter
public class UftpConnectorException extends RuntimeException {

  public UftpConnectorException(String message) {
    super(message);
  }

  public UftpConnectorException(String message, Throwable cause) {
    super(message, cause);
  }

}
