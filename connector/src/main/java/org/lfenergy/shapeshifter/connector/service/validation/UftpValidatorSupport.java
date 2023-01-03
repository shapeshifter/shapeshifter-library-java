package org.lfenergy.shapeshifter.connector.service.validation;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;

public interface UftpValidatorSupport {

  boolean isHandledRecipient(String recipientDomain, USEFRoleType role);

  boolean isBarredSender(UftpParticipant sender);

  /**
   * Gets a previously received message by messageID.
   *
   * @param messageID The message ID.
   * @param recipientDomain The recipient domain.
   * @return The message that was received previously.
   */
  Optional<PayloadMessageType> getPreviousMessage(String messageID, String recipientDomain);

  /**
   * Gets a previously sent or received message by messageID (usually for validation).
   *
   * @param messageID The message ID.
   * @param type The type of message.
   * @param <T> The type of message.
   * @return The message that was either sent or received previously.
   */
  <T extends PayloadMessageType> Optional<T> getPreviousMessage(String messageID, Class<T> type);


  boolean isSupportedIspDuration(Duration duration);

  boolean isSupportedTimeZone(String timeZone);

  boolean areKnownCongestionPoints(Collection<String> connectionPoints);

  boolean isSupportedContractID(String contractId);

  boolean isValidBaselineReference(String baselineReference);

  boolean isValidOfferOptionReference(String flexOfferMessageId, String optionReference);

  boolean isValidOrderReference(String orderReference, String recipientDomain);

  boolean existsFlexRevocation(String flexOfferMessageId, String recipientDomain);
}
