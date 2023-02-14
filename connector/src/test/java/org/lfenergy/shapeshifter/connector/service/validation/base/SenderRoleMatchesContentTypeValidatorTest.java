package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SenderRoleMatchesContentTypeValidatorTest {

  @InjectMocks
  private SenderRoleMatchesContentTypeValidator testSubject;

  @Mock
  private UftpParticipant sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(sender);
  }

  @Test
  void appliesTo_allTypes() {
    assertThat(testSubject.appliesTo(PayloadMessageType.class)).isTrue();
  }

  public static Stream<Arguments> agr_matching() {
    return Stream.of(
        Arguments.of(new AGRPortfolioQuery()),
        Arguments.of(new AGRPortfolioUpdate()),
        Arguments.of(new DPrognosis()),
        Arguments.of(new FlexOffer()),
        Arguments.of(new FlexOfferRevocation()),

        Arguments.of(new FlexOrderResponse()),
        Arguments.of(new FlexRequestResponse()),
        Arguments.of(new FlexReservationUpdateResponse()),
        Arguments.of(new FlexSettlementResponse()),

        Arguments.of(new Metering()),
        Arguments.of(new MeteringResponse()),
        Arguments.of(new TestMessage()),
        Arguments.of(new TestMessageResponse())
    );
  }

  @ParameterizedTest
  @MethodSource("agr_matching")
  void valid_agr_matches(PayloadMessageType payloadMessage) {
    given(sender.role()).willReturn(USEFRoleType.AGR);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  public static Stream<Arguments> agr_notMatching() {
    return Stream.of(
        Arguments.of(new AGRPortfolioQueryResponse()),
        Arguments.of(new AGRPortfolioUpdateResponse()),
        Arguments.of(new DSOPortfolioQueryResponse()),
        Arguments.of(new DSOPortfolioUpdateResponse()),

        Arguments.of(new DSOPortfolioQuery()),
        Arguments.of(new DSOPortfolioUpdate()),
        Arguments.of(new FlexOrder()),
        Arguments.of(new FlexRequest()),
        Arguments.of(new FlexReservationUpdate()),
        Arguments.of(new FlexSettlement()),
        Arguments.of(new DPrognosisResponse()),
        Arguments.of(new FlexOfferResponse()),
        Arguments.of(new FlexOfferRevocationResponse())
    );
  }

  @ParameterizedTest
  @MethodSource("agr_notMatching")
  void valid_agr_notMatches(PayloadMessageType payloadMessage) {
    given(sender.role()).willReturn(USEFRoleType.AGR);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  public static Stream<Arguments> dso_matching() {
    return Stream.of(
        Arguments.of(new DSOPortfolioQuery()),
        Arguments.of(new DSOPortfolioUpdate()),
        Arguments.of(new FlexOrder()),
        Arguments.of(new FlexRequest()),
        Arguments.of(new FlexReservationUpdate()),
        Arguments.of(new FlexSettlement()),

        Arguments.of(new DPrognosisResponse()),
        Arguments.of(new FlexOfferResponse()),
        Arguments.of(new FlexOfferRevocationResponse()),

        Arguments.of(new Metering()),
        Arguments.of(new MeteringResponse()),
        Arguments.of(new TestMessage()),
        Arguments.of(new TestMessageResponse())
    );
  }

  @ParameterizedTest
  @MethodSource("dso_matching")
  void valid_dso_matches(PayloadMessageType payloadMessage) {
    given(sender.role()).willReturn(USEFRoleType.DSO);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  public static Stream<Arguments> dso_notMatching() {
    return Stream.of(
        Arguments.of(new AGRPortfolioQuery()),
        Arguments.of(new AGRPortfolioUpdate()),
        Arguments.of(new DPrognosis()),
        Arguments.of(new FlexOffer()),
        Arguments.of(new FlexOfferRevocation()),

        Arguments.of(new FlexOrderResponse()),
        Arguments.of(new FlexRequestResponse()),
        Arguments.of(new FlexReservationUpdateResponse()),
        Arguments.of(new FlexSettlementResponse()),

        Arguments.of(new AGRPortfolioQueryResponse()),
        Arguments.of(new AGRPortfolioUpdateResponse()),
        Arguments.of(new DSOPortfolioQueryResponse()),
        Arguments.of(new DSOPortfolioUpdateResponse())
    );
  }

  @ParameterizedTest
  @MethodSource("dso_notMatching")
  void valid_dso_notMatches(PayloadMessageType payloadMessage) {
    given(sender.role()).willReturn(USEFRoleType.DSO);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  public static Stream<Arguments> cro_matching() {
    return Stream.of(
        Arguments.of(new AGRPortfolioQueryResponse()),
        Arguments.of(new AGRPortfolioUpdateResponse()),
        Arguments.of(new DSOPortfolioQueryResponse()),
        Arguments.of(new DSOPortfolioUpdateResponse()),

        Arguments.of(new Metering()),
        Arguments.of(new MeteringResponse()),
        Arguments.of(new TestMessage()),
        Arguments.of(new TestMessageResponse())
    );
  }

  @ParameterizedTest
  @MethodSource("cro_matching")
  void valid_cro_matches(PayloadMessageType payloadMessage) {
    given(sender.role()).willReturn(USEFRoleType.CRO);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  public static Stream<Arguments> cro_notMatching() {
    return Stream.of(
        Arguments.of(new AGRPortfolioQuery()),
        Arguments.of(new AGRPortfolioUpdate()),
        Arguments.of(new DPrognosis()),
        Arguments.of(new FlexOffer()),
        Arguments.of(new FlexOfferRevocation()),

        Arguments.of(new FlexOrderResponse()),
        Arguments.of(new FlexRequestResponse()),
        Arguments.of(new FlexReservationUpdateResponse()),
        Arguments.of(new FlexSettlementResponse()),

        Arguments.of(new DSOPortfolioQuery()),
        Arguments.of(new DSOPortfolioUpdate()),
        Arguments.of(new FlexOrder()),
        Arguments.of(new FlexRequest()),
        Arguments.of(new FlexReservationUpdate()),
        Arguments.of(new FlexSettlement()),

        Arguments.of(new DPrognosisResponse()),
        Arguments.of(new FlexOfferResponse()),
        Arguments.of(new FlexOfferRevocationResponse())
    );
  }

  @ParameterizedTest
  @MethodSource("cro_notMatching")
  void valid_cro_notMatches(PayloadMessageType payloadMessage) {
    given(sender.role()).willReturn(USEFRoleType.CRO);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Invalid Sender (not matching role)");
  }
}