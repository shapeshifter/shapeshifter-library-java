package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.lfenergy.shapeshifter.api.datetime.DateTimeCalculation.lengthOfDay;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.lfenergy.shapeshifter.api.DPrognosis;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.MeteringProfileType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.UftpBaseValidator;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;

public abstract class IspListValidatorBase implements UftpBaseValidator<PayloadMessageType> {

  private final PayloadMessagePropertyRetriever<PayloadMessageType, Boolean> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          DPrognosis.class, m -> validateIsps((DPrognosis) m),
          FlexReservationUpdate.class, m -> validateIsps((FlexReservationUpdate) m),
          FlexRequest.class, m -> validateIsps((FlexRequest) m),
          FlexOffer.class, m -> validateIsps((FlexOffer) m),
          FlexOrder.class, m -> validateIsps((FlexOrder) m),
          // Not validated due to insufficient information
          // Also might need to check against FlexOrder, but reference and msgId are missing
          // FlexSettlement.class, (m) -> validateIsps((FlexSettlement) m),
          Metering.class, m -> validateIsps((Metering) m)
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.typeInMap(clazz);
  }

  @Override
  public boolean valid(UftpMessage<PayloadMessageType> uftpMessage) {
    return retriever.getProperty(uftpMessage.payloadMessage());
  }

  private long numberOfIspsOnDay(OffsetDateTime onDay, Duration ispDuration, String ianaTimeZone) {
    var durationOfDay = lengthOfDay(onDay, ianaTimeZone);
    return durationOfDay.toSeconds() / ispDuration.toSeconds();
  }

  private long numberOfIspsOnDay(FlexMessageType msg) {
    return numberOfIspsOnDay(msg.getPeriod(), msg.getISPDuration(), msg.getTimeZone());
  }

  private boolean validateIsps(DPrognosis msg) {
    var maxNumberIsps = numberOfIspsOnDay(msg);
    var isps = msg.getISPS().stream().map(IspInfo::of).toList();
    return validateIsps(maxNumberIsps, isps);
  }

  private boolean validateIsps(FlexReservationUpdate msg) {
    var maxNumberIsps = numberOfIspsOnDay(msg);
    var isps = msg.getISPS().stream().map(IspInfo::of).toList();
    return validateIsps(maxNumberIsps, isps);
  }

  private boolean validateIsps(FlexRequest msg) {
    var maxNumberIsps = numberOfIspsOnDay(msg);
    var isps = msg.getISPS().stream().map(IspInfo::of).toList();
    return validateIsps(maxNumberIsps, isps);
  }

  private boolean validateIsps(FlexOffer msg) {
    var maxNumberIsps = numberOfIspsOnDay(msg);
    return msg.getOfferOptions().stream().allMatch(option -> validateIsps(maxNumberIsps, option));
  }

  private boolean validateIsps(long maxNumberIsps, FlexOfferOptionType option) {
    var isps = option.getISPS().stream().map(IspInfo::of).toList();
    return validateIsps(maxNumberIsps, isps);
  }

  private boolean validateIsps(FlexOrder msg) {
    var maxNumberIsps = numberOfIspsOnDay(msg);
    var isps = msg.getISPS().stream().map(IspInfo::of).toList();
    return validateIsps(maxNumberIsps, isps);
  }

  private boolean validateIsps(Metering msg) {
    var maxNumberIsps = numberOfIspsOnDay(msg.getPeriod(), msg.getISPDuration(), msg.getTimeZone());
    return msg.getProfiles().stream().allMatch(profile -> validateIsps(maxNumberIsps, profile));
  }

  private boolean validateIsps(long maxNumberIsps, MeteringProfileType profile) {
    var isps = profile.getISPS().stream().map(IspInfo::of).toList();
    return validateIsps(maxNumberIsps, isps);
  }

  protected abstract boolean validateIsps(long maxNumberIsps, List<IspInfo> isps);
}
