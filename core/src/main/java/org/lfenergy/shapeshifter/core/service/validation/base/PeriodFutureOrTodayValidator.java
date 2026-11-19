// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@RequiredArgsConstructor
public class PeriodFutureOrTodayValidator implements UftpValidator<FlexMessageType> {

  private static final String REJECTION_REASON = "Period out of bounds";

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexMessageType.class.isAssignableFrom(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public ValidationResult validate(UftpMessage<FlexMessageType> uftpMessage) {
    return ValidationResult.of(isMessageValid(uftpMessage), REJECTION_REASON);
  }

  private boolean isMessageValid(UftpMessage<FlexMessageType> uftpMessage) {
    var period = Optional.ofNullable(uftpMessage.payloadMessage().getPeriod());
    var timeZone = Optional.ofNullable(uftpMessage.payloadMessage().getTimeZone());

    return period.isEmpty() ||
        timeZone.isEmpty() ||
        isInFutureOrIsToday(period.get(), timeZone.get());
  }

  private boolean isInFutureOrIsToday(LocalDate value, String ianaTimeZone) {
    var today = ZonedDateTime.now(ZoneId.of(ianaTimeZone)).toLocalDate();

    return value.isAfter(today) || value.isEqual(today);
  }
}
