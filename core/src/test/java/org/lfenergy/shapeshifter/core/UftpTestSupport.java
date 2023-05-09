// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.lfenergy.shapeshifter.api.AGRPortfolioQuery;
import org.lfenergy.shapeshifter.api.AGRPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.AGRPortfolioUpdate;
import org.lfenergy.shapeshifter.api.AGRPortfolioUpdateResponse;
import org.lfenergy.shapeshifter.api.DPrognosis;
import org.lfenergy.shapeshifter.api.DPrognosisResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioQuery;
import org.lfenergy.shapeshifter.api.DSOPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdate;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateResponse;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferResponse;
import org.lfenergy.shapeshifter.api.FlexOfferRevocation;
import org.lfenergy.shapeshifter.api.FlexOfferRevocationResponse;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderResponse;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.FlexReservationUpdate;
import org.lfenergy.shapeshifter.api.FlexReservationUpdateResponse;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.FlexSettlementResponse;
import org.lfenergy.shapeshifter.api.Metering;
import org.lfenergy.shapeshifter.api.MeteringResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.core.common.exception.UftpConnectorException;

public class UftpTestSupport {

  public static void assertException(UftpConnectorException thrown,
                                     String message) {
    assertException(thrown, message, 500);
  }

  public static void assertExceptionCauseNotNull(UftpConnectorException thrown,
                                                 String message) {
    assertExceptionCauseNotNull(thrown, message, 500);
  }

  public static void assertException(UftpConnectorException thrown,
                                     String message, Throwable rootCause) {
    assertException(thrown, message, rootCause, 500);
  }

  public static void assertException(UftpConnectorException thrown,
                                     String message, int httpStatusCode) {
    assertThat(thrown).hasMessage(message)
                      .hasNoCause();
    assertThat(thrown.getHttpStatusCode().getValue()).isEqualTo(httpStatusCode);
  }

  public static void assertExceptionCauseNotNull(UftpConnectorException thrown,
                                                 String message, int httpStatusCode) {
    assertThat(thrown).hasMessage(message);
    assertThat(thrown.getCause()).isNotNull();
    assertThat(thrown.getHttpStatusCode().getValue()).isEqualTo(httpStatusCode);
  }

  public static void assertException(UftpConnectorException thrown,
                                     String message, Throwable rootCause,
                                     int httpStatusCode) {
    assertThat(thrown).hasMessage(message);
    assertThat(thrown.getCause()).isSameAs(rootCause);
    assertThat(thrown.getHttpStatusCode().getValue()).isEqualTo(httpStatusCode);
  }

  public static Set<Class<? extends FlexMessageType>> flexMessageTypes() {
    return Set.of(
        DPrognosis.class,
        FlexReservationUpdate.class,
        FlexRequest.class,
        FlexOffer.class,
        FlexOrder.class
    );
  }

  public static Set<Class<? extends PayloadMessageType>> allMessageTypes() {
    return Set.of(
        // The root base class
        PayloadMessageType.class,

        // Directly inherit from PayloadMessageType
        TestMessage.class,
        AGRPortfolioUpdate.class,
        AGRPortfolioQuery.class,
        FlexOfferRevocation.class,
        FlexSettlement.class,
        DSOPortfolioUpdate.class,
        DSOPortfolioQuery.class,
        Metering.class,
        PayloadMessageResponseType.class,
        FlexMessageType.class,

        // Directly inherit from PayloadMessageResponseType
        TestMessageResponse.class,
        AGRPortfolioUpdateResponse.class,
        AGRPortfolioQueryResponse.class,
        DPrognosisResponse.class,
        FlexReservationUpdateResponse.class,
        FlexRequestResponse.class,
        FlexOfferResponse.class,
        FlexOfferRevocationResponse.class,
        FlexOrderResponse.class,
        FlexSettlementResponse.class,
        DSOPortfolioUpdateResponse.class,
        DSOPortfolioQueryResponse.class,
        MeteringResponse.class,

        // Directly inherit from FlexMessageType
        DPrognosis.class,
        FlexReservationUpdate.class,
        FlexRequest.class,
        FlexOffer.class,
        FlexOrder.class
    );
  }

}
