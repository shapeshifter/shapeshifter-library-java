// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation;

import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;

/**
 * Validator that validates incoming (and outgoing) UFTP payload messages.
 *
 * <p>Beans that implement this interface automatically picked up for validation.</p>
 * <p>Application-specific validators should not implement this interface, but instead use {@link UftpUserDefinedValidator}.</p>
 *
 * <p>It is expected that every {@link UftpValidator} implementation validates a single aspect of a UFTP payload message.</p>
 *
 * @param <T> The type of UFTP payload message that can be validated by this class.
 */
public interface UftpValidator<T extends PayloadMessageType> {

  /**
   * Checks if this validator can validate the specified UFTP payload message type.
   * <p>This is needed because the generic type parameter at class level is not available at runtime.</p>
   *
   * @param clazz The type of UFTP payload message.
   * @return <code>true</code> if this validator can validate the specified UFTP payload message type, <code>false</code> otherwise.
   */
  boolean appliesTo(Class<? extends PayloadMessageType> clazz);

  /**
   * Indicates the order in which this validator should be called relative to other validators.
   *
   * @return A lower value indicates that this validator must be performed <b>before</b> another validator with a higher value. See {@link ValidationOrder} for some predefined
   * values.
   */
  int order();

  /**
   * Validates a UFTP message with the specified payload message type.
   *
   * @param uftpMessage The UFTP message.
   * @return <code>true</code> if the message is valid, <code>false</code> otherwise.
   */
  boolean isValid(UftpMessage<T> uftpMessage);

  /**
   * @return The rejection reason that must be included in the response message if the validation failed.
   */
  String getReason();
}
