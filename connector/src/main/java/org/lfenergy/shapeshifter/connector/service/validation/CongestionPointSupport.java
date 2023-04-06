// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation;

import java.util.Collection;
import org.lfenergy.shapeshifter.api.EntityAddress;

/**
 * Interface for Congestion Support <br/> <br/> Next to implementing this interface, you also have to implement other interfaces (with other concerns); the full list of the
 * interfaces is :
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
public interface CongestionPointSupport {

  /**
   * Checks whether every congestion point in a given list is a known congestion point
   *
   * @param connectionPoints The list of congestion points to be checked
   * @return Whether all given congestion points are a known congestion point
   */
  boolean areKnownCongestionPoints(Collection<EntityAddress> connectionPoints);
}
