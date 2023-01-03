package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.api.datetime.DateTimeCalculation.startOfDay;
import static org.lfenergy.shapeshifter.api.datetime.DateTimeCalculation.toZonedDateTime;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodFutureOrTodayValidator implements UftpBaseValidator<FlexMessageType> {

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return FlexMessageType.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean valid(UftpParticipant sender, FlexMessageType flexMessage) {
    var period = Optional.ofNullable(flexMessage.getPeriod());
    var timeZone = Optional.ofNullable(flexMessage.getTimeZone());

    return period.isEmpty() ||
        timeZone.isEmpty() ||
        isInFutureOrIsToday(period.get(), timeZone.get());
  }

  @Override
  public String getReason() {
    return "Period out of bounds";
  }

  private boolean isInFutureOrIsToday(OffsetDateTime value, String ianaTimeZone) {
    var period = toZonedDateTime(value, ianaTimeZone);
    var today = startOfDay(ZonedDateTime.now(ZoneId.of(ianaTimeZone)));

    return period.isAfter(today) || period.isEqual(today);
  }
}
