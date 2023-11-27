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
   * Gets a previously received message by messageID (if any) and conversation ID.
   *
   * @param messageID The message ID.
   * @param conversationId The conversation ID
   * @param recipientDomain The recipient domain.
   * @return The message that was received previously.
   */
  Optional<PayloadMessageType> getPreviousMessage(String messageID, String conversationId, String recipientDomain);

  /**
   * Gets a previously sent or received message by conversation ID and reference (usually for validation).
   *
   * @param conversationId The conversation ID
   * @param <T> The type of message.
   * @param reference The reference to the previous message.
   * @return The message that was either sent or received previously.
   */
  <T extends PayloadMessageType> Optional<T> getPreviousMessage(String conversationId, UftpMessageReference<T> reference);

  /**
   * Checks whether a given order reference is present for a given conversation ID and recipient domain
   *
   * @param orderReference The order reference to be checked
   * @param conversationId The conversation ID
   * @param recipientDomain The recipient domain for which the order reference should be checked
   * @return Whether the given order reference is present
   */
  boolean isValidOrderReference(String orderReference, String conversationId, String recipientDomain);

  /**
   * Checks whether a flex revocation is present for a conversation ID and given flexOffer message and recipient domain
   *
   * @param conversationId The conversation ID
   * @param flexOfferMessageId The flexOffer message ID
   * @param recipientDomain The recipient domain
   * @return Whether a flex revocation is present
   */
  boolean existsFlexRevocation(String conversationId, String flexOfferMessageId, String recipientDomain);
}
