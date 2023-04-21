// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.receiving;

import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;

/**
 * Base class for exceptions thrown when receiving an UFTP message fails e.g. when validation or deserialization fails, or a low level I/O error.
 */
public class UftpReceiveException extends UftpConnectorException {

  public UftpReceiveException(String message) {
    super(message);
  }

  public UftpReceiveException(String message, Throwable cause) {
    super(message, cause);
  }

}
