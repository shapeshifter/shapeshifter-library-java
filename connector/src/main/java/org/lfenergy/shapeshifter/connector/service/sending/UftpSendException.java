// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.sending;

import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;

/**
 * Base class for exceptions thrown when sending an UFTP message fails e.g. when a request fails because of an error response, or a low level I/O error.
 */
public class UftpSendException extends UftpConnectorException {

  public UftpSendException(String message) {
    super(message);
  }

  public UftpSendException(String message, Throwable cause) {
    super(message, cause);
  }

}
