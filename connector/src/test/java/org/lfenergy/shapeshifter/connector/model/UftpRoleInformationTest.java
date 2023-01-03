package org.lfenergy.shapeshifter.connector.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.model.UftpRoleInformation.getMessageTypes;
import static org.lfenergy.shapeshifter.connector.model.UftpRoleInformation.getRecipientRoleBySenderRole;
import static org.lfenergy.shapeshifter.connector.model.UftpRoleInformation.getSenderRole;

import org.junit.jupiter.api.Test;
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
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.api.USEFRoleType;

class UftpRoleInformationTest {

  @Test
  void getMessageTypes_agr() {
    assertThat(getMessageTypes(USEFRoleType.AGR)).containsExactlyInAnyOrder(
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
  }

  @Test
  void getMessageTypes_dso() {
    assertThat(getMessageTypes(USEFRoleType.DSO)).containsExactlyInAnyOrder(
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
  }

  @Test
  void getMessageTypes_cro() {
    assertThat(getMessageTypes(USEFRoleType.CRO)).containsExactlyInAnyOrder(
        AGRPortfolioQueryResponse.class,
        AGRPortfolioUpdateResponse.class,
        DSOPortfolioQueryResponse.class,
        DSOPortfolioUpdateResponse.class,
        Metering.class,
        MeteringResponse.class,
        TestMessage.class,
        TestMessageResponse.class
    );
  }

  @Test
  void getSenderRole_agr() {
    assertThat(getSenderRole(AGRPortfolioQuery.class)).isEqualTo(USEFRoleType.AGR);
  }

  @Test
  void getSenderRole_dso() {
    assertThat(getSenderRole(DSOPortfolioQuery.class)).isEqualTo(USEFRoleType.DSO);
  }

  @Test
  void getSenderRole_cro() {
    assertThat(getSenderRole(AGRPortfolioQueryResponse.class)).isEqualTo(USEFRoleType.CRO);
  }

  @Test
  void getSenderRole_invalid() {
    var thrown = assertThrows(
        IllegalArgumentException.class,
        () -> getSenderRole(TestPayloadMessageType.class)
    );
    assertThat(thrown.getMessage()).isEqualTo("Could not determine sender role for message type: " + TestPayloadMessageType.class);
  }

  @Test
  void getRecipientRoleBySenderRole_agr() {
    assertThat(getRecipientRoleBySenderRole(USEFRoleType.AGR)).isEqualTo(USEFRoleType.DSO);
  }

  @Test
  void getRecipientRoleBySenderRole_dso() {
    assertThat(getRecipientRoleBySenderRole(USEFRoleType.DSO)).isEqualTo(USEFRoleType.AGR);
  }

  @Test
  void getRecipientRoleBySenderRole_cro() {
    var thrown = assertThrows(
        IllegalArgumentException.class,
        () -> getRecipientRoleBySenderRole(USEFRoleType.CRO)
    );
    assertThat(thrown.getMessage()).isEqualTo("Cannot determine recipient role for USEFRoleType CRO");
  }

}