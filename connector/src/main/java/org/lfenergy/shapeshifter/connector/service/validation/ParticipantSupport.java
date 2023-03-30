package org.lfenergy.shapeshifter.connector.service.validation;

import org.lfenergy.shapeshifter.connector.model.UftpParticipant;

/**
 * Interface for Participant Support, contains methods with participant related concerns. <br/> <br/> Next to implementing this interface, you also have to implement other
 * interfaces (with other concerns); the full list of the interfaces is :
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
public interface ParticipantSupport {

  /**
   * Checks whether the participant information is present for a given domain and type of organisation
   *
   * @param recipient The recipient of the UFTP message, contains domain and role
   * @return Whether the participant information is present
   */
  boolean isHandledRecipient(UftpParticipant recipient);

  /**
   * Checks whether a given UFTP participant is allowed to send messages
   *
   * @param sender The UFTP participant details, domain and role
   * @return Whether the UFTP participant is allowed to send messages
   */
  boolean isAllowedSender(UftpParticipant sender);
}
