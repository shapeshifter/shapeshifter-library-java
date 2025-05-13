// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core;

import org.lfenergy.shapeshifter.api.*;

import java.util.Set;

public class UftpTestSupport {

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
