package org.lfenergy.shapeshifter.connector.service.forwarding.testmapping;

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
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpMapping;

@UftpIncomingHandler
public class UftpTestBaseMapping {

  @UftpMapping(type = AGRPortfolioQuery.class)
  public void onAGRPortfolioQuery(UftpParticipant sender, AGRPortfolioQuery message) {
  }

  @UftpMapping(type = AGRPortfolioQueryResponse.class)
  public void onAGRPortfolioQueryResponse(UftpParticipant sender, AGRPortfolioQueryResponse message) {
  }

  @UftpMapping(type = AGRPortfolioUpdate.class)
  public void onAGRPortfolioUpdate(UftpParticipant sender, AGRPortfolioUpdate message) {
  }

  @UftpMapping(type = AGRPortfolioUpdateResponse.class)
  public void onAGRPortfolioUpdateResponse(UftpParticipant sender, AGRPortfolioUpdateResponse message) {
  }

  @UftpMapping(type = DPrognosis.class)
  public void onDPrognosis(UftpParticipant sender, DPrognosis message) {
  }

  @UftpMapping(type = DPrognosisResponse.class)
  public void onDPrognosisResponse(UftpParticipant sender, DPrognosisResponse message) {
  }

  @UftpMapping(type = DSOPortfolioQuery.class)
  public void onDSOPortfolioQuery(UftpParticipant sender, DSOPortfolioQuery message) {
  }

  @UftpMapping(type = DSOPortfolioQueryResponse.class)
  public void onDSOPortfolioQueryResponse(UftpParticipant sender, DSOPortfolioQueryResponse message) {
  }

  @UftpMapping(type = DSOPortfolioUpdate.class)
  public void onDSOPortfolioUpdate(UftpParticipant sender, DSOPortfolioUpdate message) {
  }

  @UftpMapping(type = DSOPortfolioUpdateResponse.class)
  public void onDSOPortfolioUpdateResponse(UftpParticipant sender, DSOPortfolioUpdateResponse message) {
  }

  @UftpMapping(type = FlexOffer.class)
  public void onFlexOffer(UftpParticipant sender, FlexOffer message) {
  }

  @UftpMapping(type = FlexOfferResponse.class)
  public void onFlexOfferResponse(UftpParticipant sender, FlexOfferResponse message) {
  }

  @UftpMapping(type = FlexOfferRevocation.class)
  public void onFlexOfferRevocation(UftpParticipant sender, FlexOfferRevocation message) {
  }

  @UftpMapping(type = FlexOfferRevocationResponse.class)
  public void onFlexOfferRevocationResponse(UftpParticipant sender, FlexOfferRevocationResponse message) {
  }

  @UftpMapping(type = FlexOrder.class)
  public void onFlexOrder(UftpParticipant sender, FlexOrder message) {
  }

  @UftpMapping(type = FlexOrderResponse.class)
  public void onFlexOrderResponse(UftpParticipant sender, FlexOrderResponse message) {
  }

  @UftpMapping(type = FlexRequest.class)
  public void onFlexRequest(UftpParticipant sender, FlexRequest message) {
  }

  @UftpMapping(type = FlexRequestResponse.class)
  public void onFlexRequestResponse(UftpParticipant sender, FlexRequestResponse message) {
  }

  @UftpMapping(type = FlexReservationUpdate.class)
  public void onFlexReservationUpdate(UftpParticipant sender, FlexReservationUpdate message) {
  }

  @UftpMapping(type = FlexReservationUpdateResponse.class)
  public void onFlexReservationUpdateResponse(UftpParticipant sender, FlexReservationUpdateResponse message) {
  }

  @UftpMapping(type = FlexSettlement.class)
  public void onFlexSettlement(UftpParticipant sender, FlexSettlement message) {
  }

  @UftpMapping(type = FlexSettlementResponse.class)
  public void onFlexSettlementResponse(UftpParticipant sender, FlexSettlementResponse message) {
  }

  @UftpMapping(type = Metering.class)
  public void onMeteringMessage(UftpParticipant sender, Metering message) {
  }

  @UftpMapping(type = MeteringResponse.class)
  public void onMeteringResponse(UftpParticipant sender, MeteringResponse message) {
  }

  @UftpMapping(type = TestMessage.class)
  public void onTestMessage(UftpParticipant sender, TestMessage message) {
  }

  @UftpMapping(type = TestMessageResponse.class)
  public void onTestMessageResponse(UftpParticipant sender, TestMessageResponse message) {
  }
}
