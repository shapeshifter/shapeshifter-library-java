package org.lfenergy.shapeshifter.connector.service.sending;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an HTTP 4xx is received while sending an UFTP message.
 */
@SuppressWarnings("java:S110") // More than 5 parents useful and intended in this case
public final class UftpClientErrorException extends UftpSendException {

  @Getter
  private final HttpStatus httpStatus;

  public UftpClientErrorException(HttpStatus httpStatus, String message, Throwable cause) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }

}
