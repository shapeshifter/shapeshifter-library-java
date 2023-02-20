package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.api.datetime.DateTimeCalculation.ispEndInDay;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirationBeforeIspsListedEndValidator implements UftpBaseValidator<PayloadMessageType> {

  private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexRequest.class, m -> expirationAdheresToIspList((FlexRequest) m),
          FlexOffer.class, m -> expirationAdheresToIspList((FlexOffer) m)
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.typeInMap(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
    return retriever.getProperty(uftpMessage.payloadMessage());
  }

  @Override
  public String getReason() {
    return "ExpirationDateTime out of bounds (ISP's related)";
  }

  private boolean expirationAdheresToIspList(FlexRequest msg) {
    var expiration = msg.getExpirationDateTime();
    var isps = msg.getISPS().stream().map(IspInfo::of).toList();

    return expirationAdheresToIspList(expiration, List.of(isps), msg);
  }

  private boolean expirationAdheresToIspList(FlexOffer msg) {
    var expiration = msg.getExpirationDateTime();
    var isps = msg.getOfferOptions()
                  .stream()
                  .map(option -> option.getISPS().stream().map(IspInfo::of).toList())
                  .toList();

    return expirationAdheresToIspList(expiration, isps, msg);
  }

  private boolean expirationAdheresToIspList(
      OffsetDateTime expiration,
      List<List<IspInfo>> ispLists,
      FlexMessageType flexMsg
  ) {
    return expirationAdheresToIspList(expiration, ispLists, flexMsg.getPeriod(), flexMsg.getTimeZone(), flexMsg.getISPDuration());
  }

  private boolean expirationAdheresToIspList(
      OffsetDateTime expiration,
      List<List<IspInfo>> ispLists,
      OffsetDateTime period,
      String ianaTimeZone,
      Duration ispDuration
  ) {
    var lastIspNumber = findMaxIspInLists(ispLists);
    var lastIspEndMoment = ispEndInDay(period, ianaTimeZone, lastIspNumber, ispDuration);
    return expiration.isBefore(lastIspEndMoment) || expiration.isEqual(lastIspEndMoment);
  }

  private long findMaxIspInLists(List<List<IspInfo>> ispLists) {
    return ispLists.stream()
                   .mapToLong(this::findMaxIspInList)
                   .max()
                   .orElseThrow(() -> new IllegalStateException("No ISPs found"));
  }

  private long findMaxIspInList(List<IspInfo> ispInfos) {
    return ispInfos.stream()
                   .mapToLong(IspInfo::end)
                   .max()
                   .orElseThrow(() -> new IllegalStateException("No ISPs found"));
  }
}
