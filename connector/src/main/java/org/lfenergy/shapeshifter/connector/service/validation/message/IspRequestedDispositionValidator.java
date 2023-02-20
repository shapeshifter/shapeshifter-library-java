package org.lfenergy.shapeshifter.connector.service.validation.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpMessageValidator;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

/**
 * Validates that each flex request has at least one ISP with disposition REQUESTED
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IspRequestedDispositionValidator implements UftpMessageValidator<FlexRequest> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return clazz.equals(FlexRequest.class);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean valid(UftpMessage<FlexRequest> uftpMessage) {
    var flexRequest = uftpMessage.payloadMessage();
    return flexRequest.getISPS().stream().anyMatch(this::dispositionEqualsRequested);
  }

  private boolean dispositionEqualsRequested(FlexRequestISPType isp) {
    return isp.getDisposition().equals(AvailableRequestedType.REQUESTED);
  }

  @Override
  public String getReason() {
    return "Lacking Requested disposition";
  }
}
