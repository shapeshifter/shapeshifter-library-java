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
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.AGRPortfolioQueryMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.AGRPortfolioQueryResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.AGRPortfolioUpdateMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.AGRPortfolioUpdateResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.DPrognosisMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.DPrognosisResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.DSOPortfolioQueryMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.DSOPortfolioQueryResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.DSOPortfolioUpdateMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.DSOPortfolioUpdateResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexOfferMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexOfferResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexOfferRevocationMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexOfferRevocationResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexOrderMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexOrderResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexRequestResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexReservationUpdateMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexReservationUpdateResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexSettlementMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.FlexSettlementResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.MeteringMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.MeteringResponseMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.TestMessageMapping;
import org.lfenergy.shapeshifter.connector.generated.forwarding.annotation.TestMessageResponseMapping;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.forwarding.annotation.UftpIncomingHandler;

@UftpIncomingHandler
public class UftpTestDirectMapping {

  @AGRPortfolioQueryMapping
  public void onAGRPortfolioQuery(UftpParticipant sender, AGRPortfolioQuery message) {
  }

  @AGRPortfolioQueryResponseMapping
  public void onAGRPortfolioQueryResponse(UftpParticipant sender, AGRPortfolioQueryResponse message) {
  }

  @AGRPortfolioUpdateMapping
  public void onAGRPortfolioUpdate(UftpParticipant sender, AGRPortfolioUpdate message) {
  }

  @AGRPortfolioUpdateResponseMapping
  public void onAGRPortfolioUpdateResponse(UftpParticipant sender, AGRPortfolioUpdateResponse message) {
  }

  @DPrognosisMapping
  public void onDPrognosis(UftpParticipant sender, DPrognosis message) {
  }

  @DPrognosisResponseMapping
  public void onDPrognosisResponse(UftpParticipant sender, DPrognosisResponse message) {
  }

  @DSOPortfolioQueryMapping
  public void onDSOPortfolioQuery(UftpParticipant sender, DSOPortfolioQuery message) {
  }

  @DSOPortfolioQueryResponseMapping
  public void onDSOPortfolioQueryResponse(UftpParticipant sender, DSOPortfolioQueryResponse message) {
  }

  @DSOPortfolioUpdateMapping
  public void onDSOPortfolioUpdate(UftpParticipant sender, DSOPortfolioUpdate message) {
  }

  @DSOPortfolioUpdateResponseMapping
  public void onDSOPortfolioUpdateResponse(UftpParticipant sender, DSOPortfolioUpdateResponse message) {
  }

  @FlexOfferMapping
  public void onFlexOffer(UftpParticipant sender, FlexOffer message) {
  }

  @FlexOfferResponseMapping
  public void onFlexOfferResponse(UftpParticipant sender, FlexOfferResponse message) {
  }

  @FlexOfferRevocationMapping
  public void onFlexOfferRevocation(UftpParticipant sender, FlexOfferRevocation message) {
  }

  @FlexOfferRevocationResponseMapping
  public void onFlexOfferRevocationResponse(UftpParticipant sender, FlexOfferRevocationResponse message) {
  }

  @FlexOrderMapping
  public void onFlexOrder(UftpParticipant sender, FlexOrder message) {
  }

  @FlexOrderResponseMapping
  public void onFlexOrderResponse(UftpParticipant sender, FlexOrderResponse message) {
  }

  @FlexRequestMapping
  public void onFlexRequest(UftpParticipant sender, FlexRequest message) {
  }

  @FlexRequestResponseMapping
  public void onFlexRequestResponse(UftpParticipant sender, FlexRequestResponse message) {
  }

  @FlexReservationUpdateMapping
  public void onFlexReservationUpdate(UftpParticipant sender, FlexReservationUpdate message) {
  }

  @FlexReservationUpdateResponseMapping
  public void onFlexReservationUpdateResponse(UftpParticipant sender, FlexReservationUpdateResponse message) {
  }

  @FlexSettlementMapping
  public void onFlexSettlement(UftpParticipant sender, FlexSettlement message) {
  }

  @FlexSettlementResponseMapping
  public void onFlexSettlementResponse(UftpParticipant sender, FlexSettlementResponse message) {
  }

  @MeteringMapping
  public void onMeteringMessage(UftpParticipant sender, Metering message) {
  }

  @MeteringResponseMapping
  public void onMeteringResponse(UftpParticipant sender, MeteringResponse message) {
  }

  @TestMessageMapping
  public void onTestMessage(UftpParticipant sender, TestMessage message) {
  }

  @TestMessageResponseMapping
  public void onTestMessageResponse(UftpParticipant sender, TestMessageResponse message) {
  }
}
