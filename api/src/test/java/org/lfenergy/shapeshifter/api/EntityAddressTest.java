package org.lfenergy.shapeshifter.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.api.EntityAddress.Scheme;

class EntityAddressTest {

  private static final String EAN = "871685900012636543";
  private static final String EAN_XML_STRING = "ean." + EAN;

  private static final String EA1 = "2013-11.info.usef.test:001:002.090807002a&b#";
  private static final String EA1_XML_STRING = "ea1." + EA1;

  @Test
  void parseNoScheme() {
    assertThatThrownBy(() -> EntityAddress.parse("foo"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void parseEmptyAddress() {
    assertThatThrownBy(() -> EntityAddress.parse("ean."))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void parseUnsupportedScheme() {
    assertThatThrownBy(() -> EntityAddress.parse("foo.bar"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void parseEAN() {
    var entityAddress = EntityAddress.parse(EAN_XML_STRING);

    assertThat(entityAddress.getScheme()).isEqualTo(Scheme.EAN);
    assertThat(entityAddress.getAddress()).isEqualTo(EAN);
  }

  @Test
  void parseEA1() {
    var entityAddress = EntityAddress.parse(EA1_XML_STRING);

    assertThat(entityAddress.getScheme()).isEqualTo(Scheme.EA1);
    assertThat(entityAddress.getAddress()).isEqualTo(EA1);
  }

  @Test
  void toStringEAN() {
    assertThat(new EntityAddress(Scheme.EAN, EAN).toString()).isEqualTo(EAN_XML_STRING);
  }

  @Test
  void toStringEA1() {
    assertThat(new EntityAddress(Scheme.EA1, EA1).toString()).isEqualTo(EA1_XML_STRING);
  }

  @Test
  void equalsEAN() {
    assertThat(new EntityAddress(Scheme.EAN, EAN)).isEqualTo(new EntityAddress(Scheme.EAN, EAN));
  }

  @Test
  void equalsEA1() {
    assertThat(new EntityAddress(Scheme.EA1, EA1)).isEqualTo(new EntityAddress(Scheme.EA1, EA1));
  }
}