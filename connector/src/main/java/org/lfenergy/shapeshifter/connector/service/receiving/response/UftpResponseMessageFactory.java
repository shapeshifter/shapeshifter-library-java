package org.lfenergy.shapeshifter.connector.service.receiving.response;

import static lombok.AccessLevel.PRIVATE;

import java.util.Map;
import java.util.function.Supplier;
import lombok.NoArgsConstructor;
import org.lfenergy.shapeshifter.api.AGRPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.AGRPortfolioUpdateResponse;
import org.lfenergy.shapeshifter.api.DPrognosisResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioUpdateResponse;
import org.lfenergy.shapeshifter.api.FlexOfferResponse;
import org.lfenergy.shapeshifter.api.FlexOfferRevocationResponse;
import org.lfenergy.shapeshifter.api.FlexOrderResponse;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.FlexReservationUpdateResponse;
import org.lfenergy.shapeshifter.api.FlexSettlementResponse;
import org.lfenergy.shapeshifter.api.MeteringResponse;
import org.lfenergy.shapeshifter.api.ObjectFactory;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.connector.common.exception.UftpConnectorException;

@NoArgsConstructor(access = PRIVATE)
class UftpResponseMessageFactory {

  private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

  private static final Map<Class<? extends PayloadMessageResponseType>, Supplier<PayloadMessageResponseType>> RESPONSE_FACTORY_METHODS = Map.ofEntries(
      Map.entry(AGRPortfolioQueryResponse.class, OBJECT_FACTORY::createAGRPortfolioQueryResponse),
      Map.entry(AGRPortfolioUpdateResponse.class, OBJECT_FACTORY::createAGRPortfolioUpdateResponse),
      Map.entry(DPrognosisResponse.class, OBJECT_FACTORY::createDPrognosisResponse),
      Map.entry(DSOPortfolioQueryResponse.class, OBJECT_FACTORY::createDSOPortfolioQueryResponse),
      Map.entry(DSOPortfolioUpdateResponse.class, OBJECT_FACTORY::createDSOPortfolioUpdateResponse),
      Map.entry(FlexOfferResponse.class, OBJECT_FACTORY::createFlexOfferResponse),
      Map.entry(FlexOfferRevocationResponse.class, OBJECT_FACTORY::createFlexOfferRevocationResponse),
      Map.entry(FlexOrderResponse.class, OBJECT_FACTORY::createFlexOrderResponse),
      Map.entry(FlexRequestResponse.class, OBJECT_FACTORY::createFlexRequestResponse),
      Map.entry(FlexReservationUpdateResponse.class, OBJECT_FACTORY::createFlexReservationUpdateResponse),
      Map.entry(FlexSettlementResponse.class, OBJECT_FACTORY::createFlexSettlementResponse),
      Map.entry(MeteringResponse.class, OBJECT_FACTORY::createMeteringResponse)
  );

  public static PayloadMessageResponseType instantiate(Class<? extends PayloadMessageResponseType> responseType) {
    var responseObjectFactoryMethod = RESPONSE_FACTORY_METHODS.get(responseType);

    if (responseObjectFactoryMethod == null) {
      throw new UftpConnectorException("No response instance found for unsupported type " + responseType);
    }

    return responseObjectFactoryMethod.get();
  }

}
