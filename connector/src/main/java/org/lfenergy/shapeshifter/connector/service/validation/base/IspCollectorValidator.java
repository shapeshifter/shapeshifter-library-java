package org.lfenergy.shapeshifter.connector.service.validation.base;

import org.lfenergy.shapeshifter.api.DPrognosisISPType;
import org.lfenergy.shapeshifter.api.FlexOfferOptionISPType;
import org.lfenergy.shapeshifter.api.FlexOrderISPType;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.FlexReservationUpdateISPType;
import org.lfenergy.shapeshifter.api.MeteringISPType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;

public abstract class IspCollectorValidator<T extends PayloadMessageType> implements UftpBaseValidator<T> {

  protected record IspInfo(long start, long duration) {

    public static IspInfo of(FlexRequestISPType isp) {
      return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(DPrognosisISPType isp) {
      return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(FlexReservationUpdateISPType isp) {
      return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(FlexOfferOptionISPType isp) {
      return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(FlexOrderISPType isp) {
      return new IspInfo(isp.getStart(), isp.getDuration());
    }

    public static IspInfo of(MeteringISPType isp) {
      return new IspInfo(isp.getStart());
    }

    public IspInfo(Long start) {
      this(start, 1);
    }

    public long end() {
      return start + duration - 1;
    }
  }
}
