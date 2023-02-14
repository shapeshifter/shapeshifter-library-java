package org.lfenergy.shapeshifter.connector.application;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.model.UftpMessageReference;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Component;

@Component
public class UftpValidatorSupportTestImpl implements UftpValidatorSupport {

  @Override
  public boolean isHandledRecipient(String recipientDomain, USEFRoleType role) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isBarredSender(UftpParticipant sender) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Optional<PayloadMessageType> getPreviousMessage(String messageID, String recipientDomain) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T extends PayloadMessageType> Optional<T> getPreviousMessage(UftpMessageReference<T> reference) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isSupportedIspDuration(Duration duration) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isSupportedTimeZone(String timeZone) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean areKnownCongestionPoints(Collection<String> connectionPoints) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isSupportedContractID(String contractId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isValidBaselineReference(String baselineReference) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isValidOrderReference(String orderReference, String recipientDomain) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean existsFlexRevocation(String flexOfferMessageId, String recipientDomain) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
