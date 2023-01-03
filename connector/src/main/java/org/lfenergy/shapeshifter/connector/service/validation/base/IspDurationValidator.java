package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IspDurationValidator implements UftpBaseValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, Duration> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexMessageType.class, m -> ((FlexMessageType) m).getISPDuration(),
          Metering.class, m -> ((Metering) m).getISPDuration()
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.typeInMap(clazz);
  }

  @Override
  public boolean valid(UftpParticipant sender, PayloadMessageType payloadMessage) {
    var value = retriever.getOptionalProperty(payloadMessage);
    return value.isEmpty() || support.isSupportedIspDuration(value.get());
  }

  @Override
  public String getReason() {
    return "ISP duration rejected";
  }
}
