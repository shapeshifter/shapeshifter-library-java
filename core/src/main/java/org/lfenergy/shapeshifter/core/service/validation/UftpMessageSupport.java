package org.lfenergy.shapeshifter.core.service.validation;

import java.util.Optional;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessageReference;

/**
 * Interface for Uftp Message Support, contains methods dealing with UFTP messges. <br/> <br/> Next to implementing this interface, you also have to implement other interfaces
 * (with other concerns); the full list of the interfaces is :
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
public interface UftpMessageSupport {

  /**
   * Gets a previously received message by messageID (if any).
   *
   * @param messageID The message ID.
   * @param recipientDomain The recipient domain.
   * @return The message that was received previously.
   */
  Optional<PayloadMessageType> getPreviousMessage(String messageID, String recipientDomain);

  /**
   * Gets a previously sent or received message by reference (usually for validation).
   *
   * @param <T> The type of message.
   * @param reference The reference to the previous message.
   * @return The message that was either sent or received previously.
   */
  <T extends PayloadMessageType> Optional<T> getPreviousMessage(UftpMessageReference<T> reference);

  /**
   * Checks whether a given order reference is present for a given recipient domain
   *
   * @param orderReference The order reference to be checked
   * @param recipientDomain The recipient domain for which the order reference should be checked
   * @return Whether the given order reference is present
   */
  boolean isValidOrderReference(String orderReference, String recipientDomain);

  /**
   * Checks whether a flex revocation is present for a given flexOffer message and a recipient domain
   *
   * @param flexOfferMessageId The flexOffer message ID
   * @param recipientDomain The recipient domain
   * @return Whether a flex revocation is present
   */
  boolean existsFlexRevocation(String flexOfferMessageId, String recipientDomain);
}
