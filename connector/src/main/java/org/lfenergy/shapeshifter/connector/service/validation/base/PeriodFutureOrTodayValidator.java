// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.service.validation.base;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.connector.service.validation.ValidationOrder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodFutureOrTodayValidator implements UftpValidator<FlexMessageType> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexMessageType.class.isAssignableFrom(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public boolean isValid(UftpMessage<FlexMessageType> uftpMessage) {
    var period = Optional.ofNullable(uftpMessage.payloadMessage().getPeriod());
    var timeZone = Optional.ofNullable(uftpMessage.payloadMessage().getTimeZone());

    return period.isEmpty() ||
        timeZone.isEmpty() ||
        isInFutureOrIsToday(period.get(), timeZone.get());
  }

  @Override
  public String getReason() {
    return "Period out of bounds";
  }

  private boolean isInFutureOrIsToday(LocalDate value, String ianaTimeZone) {
    var today = ZonedDateTime.now(ZoneId.of(ianaTimeZone)).toLocalDate();

    return value.isAfter(today) || value.isEqual(today);
  }
}
