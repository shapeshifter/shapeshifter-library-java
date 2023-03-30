// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation;

import java.time.Duration;
import java.util.TimeZone;

/**
 * A UFTP validator interface with a number of methods that you should implement These methods typically check business logic that is specific for your system.
 * <br/>
 * <br/>
 * Next to implementing
 * this interface, you also have to implement other interfaces (with other concerns); the full list of the interfaces is :
 *
 * <ul>
 *   <li>CongestionPointSupport</li>
 *   <li>ContractSupport</li>
 *   <li>ParticipantSupport</li>
 *   <li>UftpMessageSupport</li>
 *   <li>UftpValidatorSupport</li>
 * </ul>
 *
 * @see CongestionPointSupport
 * @see ContractSupport
 * @see ParticipantSupport
 * @see UftpMessageSupport
 * @see UftpValidatorSupport
 */
public interface UftpValidatorSupport {

  /**
   * Checks whether a given duration is a supported ISP duration
   *
   * @param duration The duration which needs to be checked
   * @return Whether the duration is a supported ISP duration
   */
  boolean isSupportedIspDuration(Duration duration);

  /**
   * Checks whether a given timezone is a supported timezone
   *
   * @param timeZone The timezone which needs to be checked
   * @return Whether the timezone is a supported timezone
   */
  boolean isSupportedTimeZone(TimeZone timeZone);

  /**
   * Checks whether a given baseline reference is valid
   *
   * @param baselineReference The baseline reference to be checked
   * @return Whether the given baseline referene is valid
   */
  boolean isValidBaselineReference(String baselineReference);
}
