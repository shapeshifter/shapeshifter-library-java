package org.lfenergy.shapeshifter.api.conversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class DurationAdapterTest {

  @Test
  void parse() {
    var result = DurationAdapter.parse("PT15M");

    assertThat(result).isEqualTo(Duration.ofMinutes(15));
  }

  @Test
  void parse_null() {
    assertThat(DurationAdapter.parse(null)).isNull();
  }

  @Test
  void print() {
    var result = DurationAdapter.print(Duration.ofMinutes(15));

    assertThat(result).isEqualTo("PT15M");
  }

  @Test
  void print_null() {
    assertThat(DurationAdapter.print(null)).isNull();
  }
}
