package org.lfenergy.shapeshifter.connector.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.model.UftpRequestResponseMapping.getRequestTypeFor;
import static org.lfenergy.shapeshifter.connector.model.UftpRequestResponseMapping.getResponseTypeFor;

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

class UftpRequestResponseMappingTest {

  @Test
  void getResponseTypeFor_instance() {
    assertThat(getResponseTypeFor(new AGRPortfolioQuery())).isEqualTo(AGRPortfolioQueryResponse.class);
    assertThat(getResponseTypeFor(new AGRPortfolioUpdate())).isEqualTo(AGRPortfolioUpdateResponse.class);
    assertThat(getResponseTypeFor(new DPrognosis())).isEqualTo(DPrognosisResponse.class);
    assertThat(getResponseTypeFor(new DSOPortfolioQuery())).isEqualTo(DSOPortfolioQueryResponse.class);
    assertThat(getResponseTypeFor(new DSOPortfolioUpdate())).isEqualTo(DSOPortfolioUpdateResponse.class);
    assertThat(getResponseTypeFor(new FlexOffer())).isEqualTo(FlexOfferResponse.class);
    assertThat(getResponseTypeFor(new FlexOfferRevocation())).isEqualTo(FlexOfferRevocationResponse.class);
    assertThat(getResponseTypeFor(new FlexOrder())).isEqualTo(FlexOrderResponse.class);
    assertThat(getResponseTypeFor(new FlexRequest())).isEqualTo(FlexRequestResponse.class);
    assertThat(getResponseTypeFor(new FlexReservationUpdate())).isEqualTo(FlexReservationUpdateResponse.class);
    assertThat(getResponseTypeFor(new FlexSettlement())).isEqualTo(FlexSettlementResponse.class);
    assertThat(getResponseTypeFor(new Metering())).isEqualTo(MeteringResponse.class);
  }

  @Test
  void getResponseTypeFor_type() {
    assertThat(getResponseTypeFor(AGRPortfolioQuery.class)).isEqualTo(AGRPortfolioQueryResponse.class);
    assertThat(getResponseTypeFor(AGRPortfolioUpdate.class)).isEqualTo(AGRPortfolioUpdateResponse.class);
    assertThat(getResponseTypeFor(DPrognosis.class)).isEqualTo(DPrognosisResponse.class);
    assertThat(getResponseTypeFor(DSOPortfolioQuery.class)).isEqualTo(DSOPortfolioQueryResponse.class);
    assertThat(getResponseTypeFor(DSOPortfolioUpdate.class)).isEqualTo(DSOPortfolioUpdateResponse.class);
    assertThat(getResponseTypeFor(FlexOffer.class)).isEqualTo(FlexOfferResponse.class);
    assertThat(getResponseTypeFor(FlexOfferRevocation.class)).isEqualTo(FlexOfferRevocationResponse.class);
    assertThat(getResponseTypeFor(FlexOrder.class)).isEqualTo(FlexOrderResponse.class);
    assertThat(getResponseTypeFor(FlexRequest.class)).isEqualTo(FlexRequestResponse.class);
    assertThat(getResponseTypeFor(FlexReservationUpdate.class)).isEqualTo(FlexReservationUpdateResponse.class);
    assertThat(getResponseTypeFor(FlexSettlement.class)).isEqualTo(FlexSettlementResponse.class);
    assertThat(getResponseTypeFor(Metering.class)).isEqualTo(MeteringResponse.class);
  }

  @Test
  void getRequestTypeFor_instance() {
    assertThat(getRequestTypeFor(new AGRPortfolioQueryResponse())).isEqualTo(AGRPortfolioQuery.class);
    assertThat(getRequestTypeFor(new AGRPortfolioUpdateResponse())).isEqualTo(AGRPortfolioUpdate.class);
    assertThat(getRequestTypeFor(new DPrognosisResponse())).isEqualTo(DPrognosis.class);
    assertThat(getRequestTypeFor(new DSOPortfolioQueryResponse())).isEqualTo(DSOPortfolioQuery.class);
    assertThat(getRequestTypeFor(new DSOPortfolioUpdateResponse())).isEqualTo(DSOPortfolioUpdate.class);
    assertThat(getRequestTypeFor(new FlexOfferResponse())).isEqualTo(FlexOffer.class);
    assertThat(getRequestTypeFor(new FlexOfferRevocationResponse())).isEqualTo(FlexOfferRevocation.class);
    assertThat(getRequestTypeFor(new FlexOrderResponse())).isEqualTo(FlexOrder.class);
    assertThat(getRequestTypeFor(new FlexRequestResponse())).isEqualTo(FlexRequest.class);
    assertThat(getRequestTypeFor(new FlexReservationUpdateResponse())).isEqualTo(FlexReservationUpdate.class);
    assertThat(getRequestTypeFor(new FlexSettlementResponse())).isEqualTo(FlexSettlement.class);
    assertThat(getRequestTypeFor(new MeteringResponse())).isEqualTo(Metering.class);
  }

  @Test
  void getRequestTypeFor_type() {
    assertThat(getRequestTypeFor(AGRPortfolioQueryResponse.class)).isEqualTo(AGRPortfolioQuery.class);
    assertThat(getRequestTypeFor(AGRPortfolioUpdateResponse.class)).isEqualTo(AGRPortfolioUpdate.class);
    assertThat(getRequestTypeFor(DPrognosisResponse.class)).isEqualTo(DPrognosis.class);
    assertThat(getRequestTypeFor(DSOPortfolioQueryResponse.class)).isEqualTo(DSOPortfolioQuery.class);
    assertThat(getRequestTypeFor(DSOPortfolioUpdateResponse.class)).isEqualTo(DSOPortfolioUpdate.class);
    assertThat(getRequestTypeFor(FlexOfferResponse.class)).isEqualTo(FlexOffer.class);
    assertThat(getRequestTypeFor(FlexOfferRevocationResponse.class)).isEqualTo(FlexOfferRevocation.class);
    assertThat(getRequestTypeFor(FlexOrderResponse.class)).isEqualTo(FlexOrder.class);
    assertThat(getRequestTypeFor(FlexRequestResponse.class)).isEqualTo(FlexRequest.class);
    assertThat(getRequestTypeFor(FlexReservationUpdateResponse.class)).isEqualTo(FlexReservationUpdate.class);
    assertThat(getRequestTypeFor(FlexSettlementResponse.class)).isEqualTo(FlexSettlement.class);
    assertThat(getRequestTypeFor(MeteringResponse.class)).isEqualTo(Metering.class);
  }

  @Test
  void hasReferencedRequestMessageId() {
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(AGRPortfolioQuery.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(AGRPortfolioUpdate.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(DPrognosis.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(DSOPortfolioQuery.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(DSOPortfolioUpdate.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexOffer.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexOfferRevocation.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexOrder.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexReservationUpdate.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexSettlement.class)).isFalse();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(Metering.class)).isFalse();

    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(AGRPortfolioQueryResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(AGRPortfolioUpdateResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(DPrognosisResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(DSOPortfolioQueryResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(DSOPortfolioUpdateResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexOfferResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexOfferRevocationResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexOrderResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexRequestResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexReservationUpdateResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(FlexSettlementResponse.class)).isTrue();
    assertThat(UftpRequestResponseMapping.hasReferencedRequestMessageId(MeteringResponse.class)).isTrue();
  }

  @Test
  void getReferencedRequestMessageId_FlexRequestResponse() {
    var response = new FlexRequestResponse();
    response.setFlexRequestMessageID("aFlexRequestMessageId");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getFlexRequestMessageID());
  }

  @Test
  void getReferencedRequestMessageId_FlexOfferResponse() {
    var response = new FlexOfferResponse();
    response.setFlexOfferMessageID("aFlexOfferMessageId");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getFlexOfferMessageID());
  }

  @Test
  void getReferencedRequestMessageId_FlexOrderResponse() {
    var response = new FlexOrderResponse();
    response.setFlexOrderMessageID("aFlexOrderMessageId");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getFlexOrderMessageID());
  }

  @Test
  void getReferencedRequestMessageId_AGRPortfolioQueryResponse() {
    var response = new AGRPortfolioQueryResponse();
    response.setAGRPortfolioQueryMessageID("aAGRPortfolioQueryMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getAGRPortfolioQueryMessageID());
  }

  @Test
  void getReferencedRequestMessageId_AGRPortfolioUpdateResponse() {
    var response = new AGRPortfolioUpdateResponse();
    response.setAGRPortfolioUpdateMessageID("aAGRPortfolioUpdateMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getAGRPortfolioUpdateMessageID());
  }

  @Test
  void getReferencedRequestMessageId_DPrognosisResponse() {
    var response = new DPrognosisResponse();
    response.setDPrognosisMessageID("aDPrognosisMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getDPrognosisMessageID());
  }

  @Test
  void getReferencedRequestMessageId_DSOPortfolioQueryResponse() {
    var response = new DSOPortfolioQueryResponse();
    response.setDSOPortfolioQueryMessageID("aDSOPortfolioQueryMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getDSOPortfolioQueryMessageID());
  }

  @Test
  void getReferencedRequestMessageId_DSOPortfolioUpdateResponse() {
    var response = new DSOPortfolioUpdateResponse();
    response.setDSOPortfolioUpdateResponseMessageID("aDSOPortfolioUpdateResponseMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getDSOPortfolioUpdateResponseMessageID());
  }

  @Test
  void getReferencedRequestMessageId_FlexOfferRevocationResponse() {
    var response = new FlexOfferRevocationResponse();
    response.setFlexOfferRevocationMessageID("aFlexOfferRevocationMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getFlexOfferRevocationMessageID());
  }

  @Test
  void getReferencedRequestMessageId_FlexReservationUpdateResponse() {
    var response = new FlexReservationUpdateResponse();
    response.setFlexReservationUpdateMessageID("aFlexReservationUpdateMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getFlexReservationUpdateMessageID());
  }

  @Test
  void getReferencedRequestMessageId_FlexSettlementResponse() {
    var response = new FlexSettlementResponse();
    response.setFlexSettlementMessageID("aFlexSettlementMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getFlexSettlementMessageID());
  }

  @Test
  void getReferencedRequestMessageId_MeteringResponse() {
    var response = new MeteringResponse();
    response.setMeteringMessageID("aMeteringMessageID");

    var result = UftpRequestResponseMapping.getReferencedRequestMessageId(response);

    assertThat(result).contains(response.getMeteringMessageID());
  }

}
