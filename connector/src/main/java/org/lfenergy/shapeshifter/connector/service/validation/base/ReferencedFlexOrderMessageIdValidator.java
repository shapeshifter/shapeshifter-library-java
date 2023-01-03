package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.connector.service.validation.tools.NullablesToLinkedSet.toSetIgnoreNulls;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.DPrognosisResponse;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderStatusType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferencedFlexOrderMessageIdValidator implements UftpBaseValidator<DPrognosisResponse> {

  private final UftpValidatorSupport support;

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return DPrognosisResponse.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean valid(UftpParticipant sender, DPrognosisResponse payloadMessage) {
    var value = collectFlexOrderMessageIDs(payloadMessage);
    return value.isEmpty() || value.stream().allMatch(
        msgId -> support.getPreviousMessage(msgId, FlexOrder.class).isPresent()
    );
  }

  @Override
  public String getReason() {
    return "Unknown reference FlexOrderMessageID";
  }

  private Set<String> collectFlexOrderMessageIDs(DPrognosisResponse m) {
    return m.getFlexOrderStatuses().stream()
            .map(FlexOrderStatusType::getFlexOrderMessageID)
            .collect(toSetIgnoreNulls());
  }
}
