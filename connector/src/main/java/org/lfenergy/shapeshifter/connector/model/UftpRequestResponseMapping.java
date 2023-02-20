package org.lfenergy.shapeshifter.connector.model;

import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
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
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.service.validation.tools.PayloadMessagePropertyRetriever;

public class UftpRequestResponseMapping {

  private UftpRequestResponseMapping() {
    // Private constructor to hide implicit one
  }

  private static final Map<Class<? extends PayloadMessageType>, Class<? extends PayloadMessageResponseType>> REQUEST_TO_RESPONSE_TYPES =
      Map.ofEntries(
          Map.entry(AGRPortfolioQuery.class, AGRPortfolioQueryResponse.class),
          Map.entry(AGRPortfolioUpdate.class, AGRPortfolioUpdateResponse.class),
          Map.entry(DPrognosis.class, DPrognosisResponse.class),
          Map.entry(DSOPortfolioQuery.class, DSOPortfolioQueryResponse.class),
          Map.entry(DSOPortfolioUpdate.class, DSOPortfolioUpdateResponse.class),
          Map.entry(FlexOffer.class, FlexOfferResponse.class),
          Map.entry(FlexOfferRevocation.class, FlexOfferRevocationResponse.class),
          Map.entry(FlexOrder.class, FlexOrderResponse.class),
          Map.entry(FlexRequest.class, FlexRequestResponse.class),
          Map.entry(FlexReservationUpdate.class, FlexReservationUpdateResponse.class),
          Map.entry(FlexSettlement.class, FlexSettlementResponse.class),
          Map.entry(Metering.class, MeteringResponse.class)
// TODO TestMessageResponse must extend PayloadMessageResponseType in the XSD
//          Map.entry(TestMessage.class, TestMessageResponse.class)
      );

  private static final Map<Class<? extends PayloadMessageResponseType>, Class<? extends PayloadMessageType>> RESPONSE_TO_REQUEST_TYPES =
      Collections.unmodifiableMap(
          REQUEST_TO_RESPONSE_TYPES.entrySet().stream().collect(toMap(Map.Entry::getValue, Map.Entry::getKey))
      );

  private static final PayloadMessagePropertyRetriever<PayloadMessageType, String> REFERENCED_REQUEST_MESSAGE_ID_RETRIEVER = new PayloadMessagePropertyRetriever<>(
      Map.ofEntries(
          Map.entry(AGRPortfolioQueryResponse.class, m -> ((AGRPortfolioQueryResponse) m).getAGRPortfolioQueryMessageID()),
          Map.entry(AGRPortfolioUpdateResponse.class, m -> ((AGRPortfolioUpdateResponse) m).getAGRPortfolioUpdateMessageID()),
          Map.entry(DPrognosisResponse.class, m -> ((DPrognosisResponse) m).getDPrognosisMessageID()),
          Map.entry(DSOPortfolioQueryResponse.class, m -> ((DSOPortfolioQueryResponse) m).getDSOPortfolioQueryMessageID()),
          Map.entry(DSOPortfolioUpdateResponse.class, m -> ((DSOPortfolioUpdateResponse) m).getDSOPortfolioUpdateResponseMessageID()),
          Map.entry(FlexOfferResponse.class, m -> ((FlexOfferResponse) m).getFlexOfferMessageID()),
          Map.entry(FlexOfferRevocationResponse.class, m -> ((FlexOfferRevocationResponse) m).getFlexOfferRevocationMessageID()),
          Map.entry(FlexOrderResponse.class, m -> ((FlexOrderResponse) m).getFlexOrderMessageID()),
          Map.entry(FlexRequestResponse.class, m -> ((FlexRequestResponse) m).getFlexRequestMessageID()),
          Map.entry(FlexReservationUpdateResponse.class, m -> ((FlexReservationUpdateResponse) m).getFlexReservationUpdateMessageID()),
          Map.entry(FlexSettlementResponse.class, m -> ((FlexSettlementResponse) m).getFlexSettlementMessageID()),
          Map.entry(MeteringResponse.class, m -> ((MeteringResponse) m).getMeteringMessageID())
      )
  );

  private static final Map<Class<? extends PayloadMessageResponseType>, BiConsumer<PayloadMessageType, PayloadMessageResponseType>> REFERENCED_REQUEST_MESSAGE_ID_SETTER =
      Map.ofEntries(
          Map.entry(AGRPortfolioQueryResponse.class, (request, response) -> ((AGRPortfolioQueryResponse) response).setAGRPortfolioQueryMessageID(request.getMessageID())),
          Map.entry(AGRPortfolioUpdateResponse.class, (request, response) -> ((AGRPortfolioUpdateResponse) response).setAGRPortfolioUpdateMessageID(request.getMessageID())),
          Map.entry(DPrognosisResponse.class, (request, response) -> ((DPrognosisResponse) response).setDPrognosisMessageID(request.getMessageID())),
          Map.entry(DSOPortfolioQueryResponse.class, (request, response) -> ((DSOPortfolioQueryResponse) response).setDSOPortfolioQueryMessageID(request.getMessageID())),
          Map.entry(DSOPortfolioUpdateResponse.class,
                    (request, response) -> ((DSOPortfolioUpdateResponse) response).setDSOPortfolioUpdateResponseMessageID(request.getMessageID())),
          Map.entry(FlexOfferResponse.class, (request, response) -> ((FlexOfferResponse) response).setFlexOfferMessageID(request.getMessageID())),
          Map.entry(FlexOfferRevocationResponse.class, (request, response) -> ((FlexOfferRevocationResponse) response).setFlexOfferRevocationMessageID(request.getMessageID())),
          Map.entry(FlexOrderResponse.class, (request, response) -> ((FlexOrderResponse) response).setFlexOrderMessageID(request.getMessageID())),
          Map.entry(FlexRequestResponse.class, (request, response) -> ((FlexRequestResponse) response).setFlexRequestMessageID(request.getMessageID())),
          Map.entry(FlexReservationUpdateResponse.class,
                    (request, response) -> ((FlexReservationUpdateResponse) response).setFlexReservationUpdateMessageID(request.getMessageID())),
          Map.entry(FlexSettlementResponse.class, (request, response) -> ((FlexSettlementResponse) response).setFlexSettlementMessageID(request.getMessageID())),
          Map.entry(MeteringResponse.class, (request, response) -> ((MeteringResponse) response).setMeteringMessageID(request.getMessageID())));

  public static boolean hasReferencedRequestMessageId(Class<? extends PayloadMessageType> clazz) {
    return REFERENCED_REQUEST_MESSAGE_ID_RETRIEVER.typeInMap(clazz);
  }

  public static Optional<String> getReferencedRequestMessageId(PayloadMessageResponseType response) {
    return REFERENCED_REQUEST_MESSAGE_ID_RETRIEVER.getOptionalProperty(response);
  }

  public static void setReferencedRequestMessageId(PayloadMessageType request, PayloadMessageResponseType response) {
    REFERENCED_REQUEST_MESSAGE_ID_SETTER.get(response.getClass()).accept(request, response);
  }

  public static <T extends PayloadMessageType> Class<? extends PayloadMessageResponseType> getResponseTypeFor(T request) {
    return getResponseTypeFor(request.getClass());
  }

  public static Class<? extends PayloadMessageResponseType> getResponseTypeFor(Class<? extends PayloadMessageType> requestType) {
    return REQUEST_TO_RESPONSE_TYPES.get(requestType);
  }

  public static <T extends PayloadMessageResponseType> Class<? extends PayloadMessageType> getRequestTypeFor(T response) {
    return getRequestTypeFor(response.getClass());
  }

  public static Class<? extends PayloadMessageType> getRequestTypeFor(Class<? extends PayloadMessageResponseType> responseType) {
    return RESPONSE_TO_REQUEST_TYPES.get(responseType);
  }

}
