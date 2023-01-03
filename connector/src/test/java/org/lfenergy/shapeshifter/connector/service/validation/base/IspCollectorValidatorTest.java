package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.DPrognosisISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOrderISPType;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.FlexReservationUpdateISPType;
import org.lfenergy.shapeshifter.api.MeteringISPType;
import org.lfenergy.shapeshifter.connector.service.validation.base.IspCollectorValidator.IspInfo;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IspCollectorValidatorTest {

  @Test
  void ispInfoOfFlexRequestISPType() {
    var isp = new FlexRequestISPType();
    isp.setStart(1L);
    isp.setDuration(1L);

    var result = IspInfo.of(isp);
    assertThat(result.start()).isEqualTo(1L);
    assertThat(result.duration()).isEqualTo(1L);
    assertThat(result.end()).isEqualTo(1L);
  }

  @Test
  void ispInfoOfDPrognosisISPType() {
    var isp = new DPrognosisISPType();
    isp.setStart(8L);
    isp.setDuration(3L);

    var result = IspInfo.of(isp);
    assertThat(result.start()).isEqualTo(8L);
    assertThat(result.duration()).isEqualTo(3L);
    assertThat(result.end()).isEqualTo(10L);
  }

  @Test
  void ispInfoOfFlexReservationUpdateISPType() {
    var isp = new FlexReservationUpdateISPType();
    isp.setStart(15L);
    isp.setDuration(1L);

    var result = IspInfo.of(isp);
    assertThat(result.start()).isEqualTo(15L);
    assertThat(result.duration()).isEqualTo(1L);
    assertThat(result.end()).isEqualTo(15L);
  }

  @Test
  void ispInfoOfFlexOfferOptionISPType() {
    var isp = new FlexOfferOptionISPType();
    isp.setStart(20L);
    isp.setDuration(1L);

    var result = IspInfo.of(isp);
    assertThat(result.start()).isEqualTo(20L);
    assertThat(result.duration()).isEqualTo(1L);
    assertThat(result.end()).isEqualTo(20L);
  }

  @Test
  void ispInfoOfFlexOrderISPType() {
    var isp = new FlexOrderISPType();
    isp.setStart(20L);
    isp.setDuration(1L);

    var result = IspInfo.of(isp);
    assertThat(result.start()).isEqualTo(20L);
    assertThat(result.duration()).isEqualTo(1L);
    assertThat(result.end()).isEqualTo(20L);
  }

  @Test
  void ispInfoOfMeteringISPType() {
    var isp = new MeteringISPType();
    isp.setStart(20L);

    var result = IspInfo.of(isp);
    assertThat(result.start()).isEqualTo(20L);
    assertThat(result.duration()).isEqualTo(1L);
    assertThat(result.end()).isEqualTo(20L);
  }

  @Test
  void IspInfo_startOnly_durationAlways1() {
    IspInfo actual = new IspInfo(15L);

    assertThat(actual.start()).isEqualTo(15L);
    assertThat(actual.duration()).isEqualTo(1L);
    assertThat(actual.end()).isEqualTo(15L);
  }
}
