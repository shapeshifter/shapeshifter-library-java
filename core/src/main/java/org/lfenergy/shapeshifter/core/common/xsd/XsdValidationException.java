// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.common.xsd;

import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

/**
 * Thrown when validation against XSD failed.
 */
public class XsdValidationException extends UftpConnectorException {

  public XsdValidationException(String message) {
    super(message);
  }

  public XsdValidationException(String message, Throwable cause) {
    super(message, cause);
  }

}
