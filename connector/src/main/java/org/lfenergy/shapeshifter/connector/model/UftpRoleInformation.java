package org.lfenergy.shapeshifter.connector.model;

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
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.api.USEFRoleType;

public class UftpRoleInformation {

  private UftpRoleInformation() {
    // Private constructor to hide implicit one
  }

  private static final Set<Class<? extends PayloadMessageType>> AGR_MESSAGE_TYPES = Set.of(
      AGRPortfolioQuery.class,
      AGRPortfolioUpdate.class,
      DPrognosis.class,
      FlexOffer.class,
      FlexOfferRevocation.class,

      FlexOrderResponse.class,
      FlexRequestResponse.class,
      FlexReservationUpdateResponse.class,
      FlexSettlementResponse.class,

      Metering.class,
      MeteringResponse.class,
      TestMessage.class,
      TestMessageResponse.class
  );

  private static final Set<Class<? extends PayloadMessageType>> CRO_MESSAGE_TYPES = Set.of(
      AGRPortfolioQueryResponse.class,
      AGRPortfolioUpdateResponse.class,
      DSOPortfolioQueryResponse.class,
      DSOPortfolioUpdateResponse.class,

      Metering.class,
      MeteringResponse.class,
      TestMessage.class,
      TestMessageResponse.class
  );

  private static final Set<Class<? extends PayloadMessageType>> DSO_MESSAGE_TYPES = Set.of(
      DSOPortfolioQuery.class,
      DSOPortfolioUpdate.class,
      FlexOrder.class,
      FlexRequest.class,
      FlexReservationUpdate.class,
      FlexSettlement.class,

      DPrognosisResponse.class,
      FlexOfferResponse.class,
      FlexOfferRevocationResponse.class,

      Metering.class,
      MeteringResponse.class,
      TestMessage.class,
      TestMessageResponse.class
  );

  public static Set<Class<? extends PayloadMessageType>> getMessageTypes(USEFRoleType role) {
    return switch (role) {
      case AGR -> AGR_MESSAGE_TYPES;
      case CRO -> CRO_MESSAGE_TYPES;
      case DSO -> DSO_MESSAGE_TYPES;
    };
  }

  public static USEFRoleType getSenderRole(Class<? extends PayloadMessageType> payloadMessageType) {
    if (AGR_MESSAGE_TYPES.contains(payloadMessageType)) {
      return USEFRoleType.AGR;
    } else if (CRO_MESSAGE_TYPES.contains(payloadMessageType)) {
      return USEFRoleType.CRO;
    } else if (DSO_MESSAGE_TYPES.contains(payloadMessageType)) {
      return USEFRoleType.DSO;
    } else {
      throw new IllegalArgumentException("Could not determine sender role for message type: " + payloadMessageType);
    }
  }

  public static USEFRoleType getRecipientRoleBySenderRole(USEFRoleType role) {
    return switch (role) {
      case AGR -> USEFRoleType.DSO;
      case CRO -> throw new IllegalArgumentException("Cannot determine recipient role for USEFRoleType CRO");
      case DSO -> USEFRoleType.AGR;
    };
  }
}
