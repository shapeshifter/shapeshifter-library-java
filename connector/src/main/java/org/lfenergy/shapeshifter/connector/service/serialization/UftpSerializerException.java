// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.serialization;

import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.springframework.http.HttpStatus;

/**
 * Base class for exceptions thrown when (de)serialization of an UFTP message fails.
 */
public class UftpSerializerException extends UftpConnectorException {

  public UftpSerializerException(String message, Throwable cause, HttpStatus status) {
    super(message, cause, status);
  }

}
