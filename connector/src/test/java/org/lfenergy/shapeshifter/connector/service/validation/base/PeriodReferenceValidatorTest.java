package org.lfenergy.shapeshifter.connector.service.validation.base;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.AGRPortfolioQuery;
import org.lfenergy.shapeshifter.api.AGRPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.DSOPortfolioQuery;
import org.lfenergy.shapeshifter.api.DSOPortfolioQueryResponse;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PeriodReferenceValidatorTest {

  private static final String MATCHING_MESSAGE_ID = "MATCHING_MESSAGE_ID";
  private static final OffsetDateTime PERIOD = OffsetDateTime.of(2022, 11, 22, 0, 0, 0, 0, UTC);
  private static final OffsetDateTime OTHER = OffsetDateTime.of(2023, 12, 28, 1, 2, 3, 999, UTC);

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private PeriodReferenceValidator testSubject;

  @Mock
  private UftpParticipant sender;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        sender
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(AGRPortfolioQueryResponse.class)).isTrue();
    assertThat(testSubject.appliesTo(DSOPortfolioQueryResponse.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withParameter() {

    AGRPortfolioQueryResponse agrQueryResp = new AGRPortfolioQueryResponse();
    agrQueryResp.setAGRPortfolioQueryMessageID(MATCHING_MESSAGE_ID);
    agrQueryResp.setPeriod(PERIOD);

    DSOPortfolioQueryResponse dsoQueryResp = new DSOPortfolioQueryResponse();
    dsoQueryResp.setDSOPortfolioQueryMessageID(MATCHING_MESSAGE_ID);
    dsoQueryResp.setPeriod(PERIOD);

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setFlexRequestMessageID(MATCHING_MESSAGE_ID);
    flexOffer.setPeriod(PERIOD);

    FlexOrder flexOrder = new FlexOrder();
    flexOrder.setFlexOfferMessageID(MATCHING_MESSAGE_ID);
    flexOrder.setPeriod(PERIOD);

    return Stream.of(
        Arguments.of(agrQueryResp, AGRPortfolioQuery.class, new AGRPortfolioQuery()),
        Arguments.of(dsoQueryResp, DSOPortfolioQuery.class, new DSOPortfolioQuery()),
        Arguments.of(flexOffer, FlexRequest.class, new FlexRequest()),
        Arguments.of(flexOrder, FlexOffer.class, new FlexOffer())
    );
  }

  @Test
  void valid_true_whenFlexOfferTypeWhenThereIsNoFlexRequestMessageID() {
    // Matching Message ID is mandatory except for FlexOffer's FlexRequestMessageID
    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setFlexRequestMessageID(null);
    flexOffer.setPeriod(PERIOD);

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, flexOffer))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  <T extends PayloadMessageType> void valid_true_matchingMessageNotFound(
      PayloadMessageType payloadMessage, Class<T> matchingMessageType, T matchingMessage
  ) {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);

    given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(MATCHING_MESSAGE_ID, matchingMessageType))).willReturn(Optional.empty());

    // Matching message may not be found, although it should be there. This returns valid() == true
    // because that is not the purpose of this validation. It is checked in Referenced******MessageIdValidation classes.
    assertThat(testSubject.valid(uftpMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  <T extends PayloadMessageType> void valid_true_matchingMessageFoundPeriodMatches(
      PayloadMessageType payloadMessage, Class<T> matchingMessageType, T matchingMessage
  ) throws Exception {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);

    setPeriod(matchingMessageType, matchingMessage, PERIOD);
    given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(MATCHING_MESSAGE_ID, matchingMessageType))).willReturn(Optional.of(matchingMessage));

    assertThat(testSubject.valid(uftpMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  <T extends PayloadMessageType> void valid_false_matchingMessageFoundPeriodDoesNotMatch(
      PayloadMessageType payloadMessage, Class<T> matchingMessageType, T matchingMessage
  ) throws Exception {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);

    setPeriod(matchingMessageType, matchingMessage, OTHER);
    given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(MATCHING_MESSAGE_ID, matchingMessageType))).willReturn(Optional.of(matchingMessage));

    assertThat(testSubject.valid(uftpMessage)).isFalse();
  }

  private <T extends PayloadMessageType> void setPeriod(Class<T> matchingMessageType, T matchingMessage, OffsetDateTime value) throws Exception {
    Method setter = null;
    try {
      setter = matchingMessageType.getDeclaredMethod("setPeriod", OffsetDateTime.class);
    } catch (NoSuchMethodException e) {
      setter = matchingMessageType.getSuperclass().getDeclaredMethod("setPeriod", OffsetDateTime.class);
    }
    setter.invoke(matchingMessage, value);
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Reference Period mismatch");
  }
}