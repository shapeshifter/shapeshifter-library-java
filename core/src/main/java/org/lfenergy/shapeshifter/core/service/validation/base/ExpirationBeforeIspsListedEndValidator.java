// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import static org.lfenergy.shapeshifter.api.datetime.DateTimeCalculation.ispEndInDay;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.tools.PayloadMessagePropertyRetriever;

public class ExpirationBeforeIspsListedEndValidator implements UftpValidator<PayloadMessageType> {

  private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          FlexRequest.class, m -> expirationAdheresToIspList((FlexRequest) m),
          FlexOffer.class, m -> expirationAdheresToIspList((FlexOffer) m)
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.isTypeInMap(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_MESSAGE_SPECIFIC;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    return retriever.getProperty(uftpMessage.payloadMessage());
  }

  @Override
  public String getReason() {
    return "ExpirationDateTime out of bounds (ISPs related)";
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
      LocalDate period,
      String ianaTimeZone,
      Duration ispDuration
  ) {
    var lastIspNumber = findMaxIspInLists(ispLists);
    var lastIspEndMoment = ispEndInDay(period, ianaTimeZone, lastIspNumber, ispDuration);
    return expiration.isBefore(OffsetDateTime.from(lastIspEndMoment)) || expiration.isEqual(OffsetDateTime.from(lastIspEndMoment));
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
