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
 * Validates that all ISP's that have disposition = requested indicate a power direction, thus having either a minPower &lt; 0 *or* a maxPower &gt; 0 but not both.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IspPowerDiscrepancyValidator implements UftpMessageValidator<FlexRequest> {

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
    return flexRequest.getISPS().stream().filter(this::dispositionEqualsRequested).noneMatch(this::powerHasNoDirection);
  }

  private boolean dispositionEqualsRequested(FlexRequestISPType isp) {
    return isp.getDisposition().equals(AvailableRequestedType.REQUESTED);
  }

  // We say that power has no direction if both min power < 0 and max power > 0; in that case
  // we have a flex request message that suggests that it's both REQUESTED and AVAILABLE
  // at the same time....
  private boolean powerHasNoDirection(FlexRequestISPType isp) {
    return isp.getMinPower() < 0 && isp.getMaxPower() > 0;
  }

  @Override
  public String getReason() {
    return "One or more ISPs with a 'Requested' disposition has no direction";
  }
}
