package org.lfenergy.shapeshifter.connector.service.forwarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.shapeshifter.connector.UftpTestSupport.assertException;

import java.util.List;
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
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestBaseMapping;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestDirectMapping;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorAbstractMessageTypeSecondArgOnMethod;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorDoubleMethodMapping;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorNoMessageTypeSecondArgOnMethod;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorNoneVoidReturnTypeOnMethod;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorNotMatchingTypeSecondArgOnMethod;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorOneArgOnMethod;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorThreeArgsOnMethod;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorWrongFirstArgOnMethod;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorWrongSecondArgOnMethod;
import org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestNoUftpMappings;

class UftpForwardingMappingTest {

  private Object uftpIncomingMappingClass;
  private Object uftpOutgoingMappingClass;

  private UftpForwardingMapping testSubject;

  @Test
  void discoverDirectMapping() throws Throwable {
    uftpIncomingMappingClass = new UftpTestDirectMapping();
    uftpOutgoingMappingClass = new UftpTestDirectMapping();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    testSubject.discover();

    testTypeMapping(AGRPortfolioQuery.class, "onAGRPortfolioQuery");
    testTypeMapping(AGRPortfolioQueryResponse.class, "onAGRPortfolioQueryResponse");
    testTypeMapping(AGRPortfolioUpdate.class, "onAGRPortfolioUpdate");
    testTypeMapping(AGRPortfolioUpdateResponse.class, "onAGRPortfolioUpdateResponse");
    testTypeMapping(DPrognosis.class, "onDPrognosis");
    testTypeMapping(DPrognosisResponse.class, "onDPrognosisResponse");
    testTypeMapping(DSOPortfolioQuery.class, "onDSOPortfolioQuery");
    testTypeMapping(DSOPortfolioQueryResponse.class, "onDSOPortfolioQueryResponse");
    testTypeMapping(DSOPortfolioUpdate.class, "onDSOPortfolioUpdate");
    testTypeMapping(DSOPortfolioUpdateResponse.class, "onDSOPortfolioUpdateResponse");
    testTypeMapping(FlexOffer.class, "onFlexOffer");
    testTypeMapping(FlexOfferResponse.class, "onFlexOfferResponse");
    testTypeMapping(FlexOfferRevocation.class, "onFlexOfferRevocation");
    testTypeMapping(FlexOfferRevocationResponse.class, "onFlexOfferRevocationResponse");
    testTypeMapping(FlexOrder.class, "onFlexOrder");
    testTypeMapping(FlexOrderResponse.class, "onFlexOrderResponse");
    testTypeMapping(FlexRequest.class, "onFlexRequest");
    testTypeMapping(FlexRequestResponse.class, "onFlexRequestResponse");
    testTypeMapping(FlexReservationUpdate.class, "onFlexReservationUpdate");
    testTypeMapping(FlexReservationUpdateResponse.class, "onFlexReservationUpdateResponse");
    testTypeMapping(FlexSettlement.class, "onFlexSettlement");
    testTypeMapping(FlexSettlementResponse.class, "onFlexSettlementResponse");
    testTypeMapping(Metering.class, "onMeteringMessage");
    testTypeMapping(MeteringResponse.class, "onMeteringResponse");
  }

  @Test
  void discoverBaseMapping() throws Throwable {
    uftpIncomingMappingClass = new UftpTestBaseMapping();
    uftpOutgoingMappingClass = new UftpTestBaseMapping();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    testSubject.discover();

    testTypeMapping(AGRPortfolioQuery.class, "onAGRPortfolioQuery");
    testTypeMapping(AGRPortfolioQueryResponse.class, "onAGRPortfolioQueryResponse");
    testTypeMapping(AGRPortfolioUpdate.class, "onAGRPortfolioUpdate");
    testTypeMapping(AGRPortfolioUpdateResponse.class, "onAGRPortfolioUpdateResponse");
    testTypeMapping(DPrognosis.class, "onDPrognosis");
    testTypeMapping(DPrognosisResponse.class, "onDPrognosisResponse");
    testTypeMapping(DSOPortfolioQuery.class, "onDSOPortfolioQuery");
    testTypeMapping(DSOPortfolioQueryResponse.class, "onDSOPortfolioQueryResponse");
    testTypeMapping(DSOPortfolioUpdate.class, "onDSOPortfolioUpdate");
    testTypeMapping(DSOPortfolioUpdateResponse.class, "onDSOPortfolioUpdateResponse");
    testTypeMapping(FlexOffer.class, "onFlexOffer");
    testTypeMapping(FlexOfferResponse.class, "onFlexOfferResponse");
    testTypeMapping(FlexOfferRevocation.class, "onFlexOfferRevocation");
    testTypeMapping(FlexOfferRevocationResponse.class, "onFlexOfferRevocationResponse");
    testTypeMapping(FlexOrder.class, "onFlexOrder");
    testTypeMapping(FlexOrderResponse.class, "onFlexOrderResponse");
    testTypeMapping(FlexRequest.class, "onFlexRequest");
    testTypeMapping(FlexRequestResponse.class, "onFlexRequestResponse");
    testTypeMapping(FlexReservationUpdate.class, "onFlexReservationUpdate");
    testTypeMapping(FlexReservationUpdateResponse.class, "onFlexReservationUpdateResponse");
    testTypeMapping(FlexSettlement.class, "onFlexSettlement");
    testTypeMapping(FlexSettlementResponse.class, "onFlexSettlementResponse");
    testTypeMapping(Metering.class, "onMeteringMessage");
    testTypeMapping(MeteringResponse.class, "onMeteringResponse");
  }

  @Test
  void discoverNoMappings() throws Throwable {
    uftpIncomingMappingClass = new UftpTestNoUftpMappings();
    uftpOutgoingMappingClass = new UftpTestNoUftpMappings();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    testSubject.discover();

    testEmptyTypeMapping(AGRPortfolioQuery.class);
    testEmptyTypeMapping(AGRPortfolioQueryResponse.class);
    testEmptyTypeMapping(AGRPortfolioUpdate.class);
    testEmptyTypeMapping(AGRPortfolioUpdateResponse.class);
    testEmptyTypeMapping(DPrognosis.class);
    testEmptyTypeMapping(DPrognosisResponse.class);
    testEmptyTypeMapping(DSOPortfolioQuery.class);
    testEmptyTypeMapping(DSOPortfolioQueryResponse.class);
    testEmptyTypeMapping(DSOPortfolioUpdate.class);
    testEmptyTypeMapping(DSOPortfolioUpdateResponse.class);
    testEmptyTypeMapping(FlexOffer.class);
    testEmptyTypeMapping(FlexOfferResponse.class);
    testEmptyTypeMapping(FlexOfferRevocation.class);
    testEmptyTypeMapping(FlexOfferRevocationResponse.class);
    testEmptyTypeMapping(FlexOrder.class);
    testEmptyTypeMapping(FlexOrderResponse.class);
    testEmptyTypeMapping(FlexRequest.class);
    testEmptyTypeMapping(FlexRequestResponse.class);
    testEmptyTypeMapping(FlexReservationUpdate.class);
    testEmptyTypeMapping(FlexReservationUpdateResponse.class);
    testEmptyTypeMapping(FlexSettlement.class);
    testEmptyTypeMapping(FlexSettlementResponse.class);
    testEmptyTypeMapping(Metering.class);
    testEmptyTypeMapping(MeteringResponse.class);
  }

  private void testTypeMapping(Class<? extends PayloadMessageType> type, String methodName) {
    testIncomingTypeMapping(type, methodName);
    testOutgoingTypeMapping(type, methodName);
  }

  private void testIncomingTypeMapping(Class<? extends PayloadMessageType> type, String methodName) {
    var result = testSubject.findIncomingHandler(type);

    assertThat(result).isPresent();
    assertThat(result.get().bean()).isSameAs(uftpIncomingMappingClass);
    assertThat(result.get().method().getName()).isEqualTo(methodName);
  }

  private void testOutgoingTypeMapping(Class<? extends PayloadMessageType> type, String methodName) {
    var result = testSubject.findOutgoingHandler(type);

    assertThat(result).isPresent();
    assertThat(result.get().bean()).isSameAs(uftpOutgoingMappingClass);
    assertThat(result.get().method().getName()).isEqualTo(methodName);
  }

  private void testEmptyTypeMapping(Class<? extends PayloadMessageType> type) {
    var incomingResult = testSubject.findIncomingHandler(type);
    assertThat(incomingResult).isEmpty();

    var outgoingResult = testSubject.findOutgoingHandler(type);
    assertThat(outgoingResult).isEmpty();
  }

  @Test
  void discoverError_oneArgOnMethod() {
    uftpIncomingMappingClass = new UftpTestMappingErrorOneArgOnMethod();
    uftpOutgoingMappingClass = new UftpTestMappingErrorOneArgOnMethod();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual,
                    "Method onFlexRequest signature must be \"void onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant, org.lfenergy.shapeshifter.api.FlexRequest)\". Found signature: \"public void org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorOneArgOnMethod.onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant)\"");
  }


  @Test
  void discoverError_threeArgsOnMethod() {
    uftpIncomingMappingClass = new UftpTestMappingErrorThreeArgsOnMethod();
    uftpOutgoingMappingClass = new UftpTestMappingErrorThreeArgsOnMethod();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual,
                    "Method onFlexRequest signature must be \"void onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant, org.lfenergy.shapeshifter.api.FlexRequest)\". Found signature: \"public void org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorThreeArgsOnMethod.onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant,org.lfenergy.shapeshifter.api.FlexRequest,java.lang.String)\"");
  }

  @Test
  void discoverError_wrongFirstArgOnMethod() {
    uftpIncomingMappingClass = new UftpTestMappingErrorWrongFirstArgOnMethod();
    uftpOutgoingMappingClass = new UftpTestMappingErrorWrongFirstArgOnMethod();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual,
                    "Method onFlexRequest signature must be \"void onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant, org.lfenergy.shapeshifter.api.FlexRequest)\". Found signature: \"public void org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorWrongFirstArgOnMethod.onFlexRequest(java.lang.String,org.lfenergy.shapeshifter.api.FlexRequest)\"");
  }

  @Test
  void discoverError_wrongSecondArgOnMethod() {
    uftpIncomingMappingClass = new UftpTestMappingErrorWrongSecondArgOnMethod();
    uftpOutgoingMappingClass = new UftpTestMappingErrorWrongSecondArgOnMethod();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual,
                    "Method onFlexRequest signature must be \"void onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant, org.lfenergy.shapeshifter.api.FlexRequest)\". Found signature: \"public void org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorWrongSecondArgOnMethod.onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant,java.lang.String)\"");
  }

  @Test
  void discoverError_notMatchingTypeSecondArgOnMethod() {
    uftpIncomingMappingClass = new UftpTestMappingErrorNotMatchingTypeSecondArgOnMethod();
    uftpOutgoingMappingClass = new UftpTestMappingErrorNotMatchingTypeSecondArgOnMethod();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual,
                    "Method onFlexRequest signature must be \"void onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant, org.lfenergy.shapeshifter.api.FlexRequest)\". Found signature: \"public void org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorNotMatchingTypeSecondArgOnMethod.onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant,org.lfenergy.shapeshifter.api.FlexOrder)\"");
  }

  @Test
  void discoverError_abstractMessageTypeSecondArgOnMethod() {
    uftpIncomingMappingClass = new UftpTestMappingErrorAbstractMessageTypeSecondArgOnMethod();
    uftpOutgoingMappingClass = new UftpTestMappingErrorAbstractMessageTypeSecondArgOnMethod();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual, "Abstract type org.lfenergy.shapeshifter.api.FlexMessageType is not supported");
  }

  @Test
  void discoverError_noMessageTypeSecondArgOnMethod() {
    uftpIncomingMappingClass = new UftpTestMappingErrorNoMessageTypeSecondArgOnMethod();
    uftpOutgoingMappingClass = new UftpTestMappingErrorNoMessageTypeSecondArgOnMethod();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual, "Abstract type org.lfenergy.shapeshifter.api.PayloadMessageType is not supported");
  }

  @Test
  void discoverError_noneVoidReturnTypeOnMethod() {
    uftpIncomingMappingClass = new UftpTestMappingErrorNoneVoidReturnTypeOnMethod();
    uftpOutgoingMappingClass = new UftpTestMappingErrorNoneVoidReturnTypeOnMethod();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual,
                    "Method onFlexRequest signature must be \"void onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant, org.lfenergy.shapeshifter.api.FlexRequest)\". Found signature: \"public java.lang.String org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorNoneVoidReturnTypeOnMethod.onFlexRequest(org.lfenergy.shapeshifter.connector.model.UftpParticipant,org.lfenergy.shapeshifter.api.FlexRequest)\"");
  }

  @Test
  void discoverError_doubleMethodMapping() {
    uftpIncomingMappingClass = new UftpTestMappingErrorDoubleMethodMapping();
    uftpOutgoingMappingClass = new UftpTestMappingErrorDoubleMethodMapping();
    testSubject = new UftpForwardingMapping(List.of(uftpIncomingMappingClass), List.of(uftpOutgoingMappingClass));

    UftpConnectorException actual = assertThrows(UftpConnectorException.class, () -> testSubject.discover());

    assertException(actual,
                    "Method org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorDoubleMethodMapping:onFlexRequestBase is already registered to handle incoming type org.lfenergy.shapeshifter.api.FlexRequest. Found second method: org.lfenergy.shapeshifter.connector.service.forwarding.testmapping.UftpTestMappingErrorDoubleMethodMapping:onFlexRequestDirect");
  }
}