package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.time.OffsetDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirationInTheFutureValidator implements UftpBaseValidator<PayloadMessageType> {

  private final PayloadMessagePropertyRetriever<PayloadMessageType, OffsetDateTime> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexRequest.class, m -> ((FlexRequest) m).getExpirationDateTime(),
          FlexOffer.class, m -> ((FlexOffer) m).getExpirationDateTime()
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.typeInMap(clazz);
  }

  @Override
  public boolean valid(UftpParticipant sender, PayloadMessageType payloadMessage) {
    var value = retriever.getOptionalProperty(payloadMessage);
    return value.isEmpty() || isInFuture(value.get());
  }

  private boolean isInFuture(OffsetDateTime expirationMoment) {
    var now = OffsetDateTime.now();
    return expirationMoment.isAfter(now);
  }

  @Override
  public String getReason() {
    return "ExpirationDateTime out of bounds";
  }
}
