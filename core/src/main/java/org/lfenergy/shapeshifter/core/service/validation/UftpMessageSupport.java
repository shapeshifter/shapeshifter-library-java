package org.lfenergy.shapeshifter.core.service.validation;

import java.util.Optional;

import org.lfenergy.shapeshifter.api.FlexOfferRevocation;
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
   * Finds an existing received message by messageID (if any) and and sender domain and recipient domain
   *
   * @param messageID The message ID.
   * @param senderDomain The sender domain.
   * @param recipientDomain The recipient domain.
   * @return The message that was received previously.
   */
  Optional<PayloadMessageType> findDuplicateMessage(String messageID, String senderDomain, String recipientDomain);

  /**
   * Gets a previously sent or received message by reference (usually for validation), for example
   *
   * @param <T> The type of message.
   * @param reference The reference to the previous message.
   * @return The message that was either sent or received previously.
   */
  <T extends PayloadMessageType> Optional<T> findReferencedMessage(UftpMessageReference<T> reference);

  /**
   * Checks whether a given order reference is present for a given conversation ID and recipient domain
   *
   * @param orderReference The order reference to be checked
   * @param conversationID The conversation ID
   * @param recipientDomain The recipient domain for which the order reference should be checked
   * @return Whether the given order reference is present
   */
  boolean isValidOrderReference(String orderReference, String conversationID, String recipientDomain);

  /**
   * Checks whether a flex revocation is present for a conversation ID and given flexOffer message and recipient domain
   *
   * @param conversationID The conversation ID
   * @param flexOfferMessageID The flexOffer message ID
   * @param senderDomain The sender domain
   * @param recipientDomain The recipient domain
   * @return Whether a flex revocation is present
   */
  Optional<FlexOfferRevocation> findFlexRevocation(String conversationID, String flexOfferMessageID, String senderDomain, String recipientDomain);
}
