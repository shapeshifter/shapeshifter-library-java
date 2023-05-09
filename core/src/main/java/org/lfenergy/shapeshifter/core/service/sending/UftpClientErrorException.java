// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.sending;

import org.lfenergy.shapeshifter.core.common.HttpStatusCode;

/**
 * Exception thrown when an HTTP 4xx is received while sending an UFTP message.
 */
@SuppressWarnings("java:S110") // More than 5 parents useful and intended in this case
public final class UftpClientErrorException extends UftpSendException {

  public UftpClientErrorException(String message, HttpStatusCode httpStatusCode) {
    super(message, httpStatusCode);
  }

}
