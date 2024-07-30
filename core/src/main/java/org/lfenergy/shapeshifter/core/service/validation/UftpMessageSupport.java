package org.lfenergy.shapeshifter.core.service.validation;

import org.lfenergy.shapeshifter.api.FlexOfferRevocation;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessageReference;

import java.util.Optional;

/**
 * Interface for UFTP Message Support, contains methods dealing with UFTP messages.
 * <br/> <br/>
 * Next to implementing this interface, you also have to implement other interfaces
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
     * Finds an existing received message by messageID (if any) and sender domain and recipient domain
     *
     * @param messageID       The message ID.
     * @param senderDomain    The sender domain.
     * @param recipientDomain The recipient domain.
     * @return The message that was received previously.
     */
    Optional<PayloadMessageType> findDuplicateMessage(String messageID, String senderDomain, String recipientDomain);

    /**
     * Gets a previously sent or received message by reference (usually for validation), for example
     *
     * @param <T>       The type of message.
     * @param reference The reference to the previous message.
     * @return The message that was either sent or received previously.
     */
    <T extends PayloadMessageType> Optional<T> findReferencedMessage(UftpMessageReference<T> reference);

    /**
     * Checks whether a flex revocation is present for a conversation ID and given flexOffer message and recipient domain
     *
     * @param conversationID     The conversation ID
     * @param flexOfferMessageID The flexOffer message ID
     * @param senderDomain       The sender domain
     * @param recipientDomain    The recipient domain
     * @return Whether a flex revocation is present
     */
    Optional<FlexOfferRevocation> findFlexRevocation(String conversationID, String flexOfferMessageID, String senderDomain, String recipientDomain);
}
