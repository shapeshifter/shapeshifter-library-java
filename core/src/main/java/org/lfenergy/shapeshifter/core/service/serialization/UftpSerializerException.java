// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.serialization;

import org.lfenergy.shapeshifter.core.common.HttpStatusCode;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

/**
 * Base class for exceptions thrown when (de)serialization of an UFTP message fails.
 */
public class UftpSerializerException extends UftpConnectorException {

  public UftpSerializerException(String message, Throwable cause, HttpStatusCode status) {
    super(message, status, cause);
  }

}
