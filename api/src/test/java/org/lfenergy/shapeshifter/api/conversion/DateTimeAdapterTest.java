package org.lfenergy.shapeshifter.api.conversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class DateTimeAdapterTest {

  @Test
  public void parseNull() {
    OffsetDateTime parsed = DateTimeAdapter.parse(null);
    assertThat(parsed).isNull();

    String printed = DateTimeAdapter.print(null);
    assertThat(printed).isNull();
  }

  @Test
  public void noPartial() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.UTC);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.UTC);

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58Z");
  }

  @Test
  public void withTenths() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58.3");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getNano()).isEqualTo(300000000);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.UTC);

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58.3Z");
  }

  @Test
  public void withMillis() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58.123");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getNano()).isEqualTo(123000000);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.UTC);

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58.123Z");
  }

  @Test
  public void withMicros() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58.123456");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getNano()).isEqualTo(123456000);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.UTC);

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58.123456Z");
  }

  @Test
  public void zulu() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58.123Z");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getNano()).isEqualTo(123000000);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.UTC);

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58.123Z");
  }

  @Test
  public void minusHours() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58.123-06:00");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getNano()).isEqualTo(123000000);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.ofHours(-6));

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58.123-06:00");
  }

  @Test
  public void minusHoursAndMinutes() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58.123-09:30");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getNano()).isEqualTo(123000000);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.ofHoursMinutes(-9, -30));

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58.123-09:30");
  }

  @Test
  public void plusHours() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58.123+02:00");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getNano()).isEqualTo(123000000);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.ofHours(2));

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58.123+02:00");
  }

  @Test
  public void plusHoursAndMinutes() {
    OffsetDateTime parsed = DateTimeAdapter.parse("2020-04-22T11:43:58.123+04:15");
    assertThat(parsed.getYear()).isEqualTo(2020);
    assertThat(parsed.getMonth()).isEqualTo(Month.APRIL);
    assertThat(parsed.getDayOfMonth()).isEqualTo(22);
    assertThat(parsed.getHour()).isEqualTo(11);
    assertThat(parsed.getMinute()).isEqualTo(43);
    assertThat(parsed.getSecond()).isEqualTo(58);
    assertThat(parsed.getNano()).isEqualTo(123000000);
    assertThat(parsed.getOffset()).isEqualTo(ZoneOffset.ofHoursMinutes(4, 15));

    String printed = DateTimeAdapter.print(parsed);
    assertThat(printed).isEqualTo("2020-04-22T11:43:58.123+04:15");
  }
}