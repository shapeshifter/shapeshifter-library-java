package org.lfenergy.shapeshifter.api.xsdinfo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UftpXsdsTest {

  @Test
  void resourcesAreFoundOnClassPath() {
    assertThat(UftpXsds.values()).allSatisfy(value -> assertThat(value.getUrl()).isNotNull());
  }
}
