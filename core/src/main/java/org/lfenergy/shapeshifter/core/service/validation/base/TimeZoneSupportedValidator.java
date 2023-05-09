// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import java.util.Map;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.AGRPortfolioQuery;
import org.lfenergy.shapeshifter.api.AGRPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.AGRPortfolioUpdate;
import org.lfenergy.shapeshifter.api.DSOPortfolioQuery;
import org.lfenergy.shapeshifter.api.DSOPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdate;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.core.service.validation.ValidationOrder;
import org.lfenergy.shapeshifter.core.service.validation.tools.PayloadMessagePropertyRetriever;


@RequiredArgsConstructor
public class TimeZoneSupportedValidator implements UftpValidator<PayloadMessageType> {

  private final UftpValidatorSupport support;
  private final PayloadMessagePropertyRetriever<PayloadMessageType, String> retriever = new PayloadMessagePropertyRetriever<>(
      Map.of(
          AGRPortfolioUpdate.class, m -> ((AGRPortfolioUpdate) m).getTimeZone(),
          AGRPortfolioQuery.class, m -> ((AGRPortfolioQuery) m).getTimeZone(),
          AGRPortfolioQueryResponse.class, m -> ((AGRPortfolioQueryResponse) m).getTimeZone(),
          FlexMessageType.class, m -> ((FlexMessageType) m).getTimeZone(),
          DSOPortfolioUpdate.class, m -> ((DSOPortfolioUpdate) m).getTimeZone(),
          DSOPortfolioQuery.class, m -> ((DSOPortfolioQuery) m).getTimeZone(),
          DSOPortfolioQueryResponse.class, m -> ((DSOPortfolioQueryResponse) m).getTimeZone(),
          Metering.class, m -> ((Metering) m).getTimeZone()
      )
  );

  @Override
  public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
    return retriever.isTypeInMap(clazz);
  }

  @Override
  public int order() {
    return ValidationOrder.SPEC_FLEX_MESSAGE;
  }

  @Override
  public boolean isValid(UftpMessage<PayloadMessageType> uftpMessage) {
    var value = retriever.getOptionalProperty(uftpMessage.payloadMessage());
    return value.isEmpty() || support.isSupportedTimeZone(TimeZone.getTimeZone(value.get()));
  }

  @Override
  public String getReason() {
    return "Time zone rejected";
  }
}
