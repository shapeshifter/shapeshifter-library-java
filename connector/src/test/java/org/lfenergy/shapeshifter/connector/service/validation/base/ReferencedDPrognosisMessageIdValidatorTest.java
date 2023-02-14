package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.DPrognosis;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderSettlementType;
import org.lfenergy.shapeshifter.api.FlexSettlement;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedDPrognosisMessageIdValidatorTest {

  private static final String DPROGNOSIS_MESSAGE_ID1 = "DPROGNOSIS_MESSAGE_ID1";
  private static final String DPROGNOSIS_MESSAGE_ID2 = "DPROGNOSIS_MESSAGE_ID2";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private ReferencedDPrognosisMessageIdValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private DPrognosis dPrognosisType1, dPrognosisType2;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        sender,
        dPrognosisType1, dPrognosisType2
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
    assertThat(testSubject.appliesTo(FlexSettlement.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  public static Stream<Arguments> withoutParameter() {
    FlexSettlement noSubElements = new FlexSettlement();

    FlexSettlement noDProgMsgIdOnFlexOrderSettlement = new FlexSettlement();
    noDProgMsgIdOnFlexOrderSettlement.getFlexOrderSettlements().addAll(
        List.of(new FlexOrderSettlementType(), new FlexOrderSettlementType())
    );

    return Stream.of(
        Arguments.of(new FlexOffer()),
        Arguments.of(new FlexOrder()),
        Arguments.of(noSubElements),
        Arguments.of(noDProgMsgIdOnFlexOrderSettlement)
    );
  }

  public static Stream<Arguments> withParameter() {

    FlexOffer flexOffer = new FlexOffer();
    flexOffer.setDPrognosisMessageID(DPROGNOSIS_MESSAGE_ID1);

    FlexOrder flexOrder = new FlexOrder();
    flexOrder.setDPrognosisMessageID(DPROGNOSIS_MESSAGE_ID1);

    FlexOrderSettlementType os1 = new FlexOrderSettlementType();
    os1.setDPrognosisMessageID(DPROGNOSIS_MESSAGE_ID1);
    FlexOrderSettlementType os2 = new FlexOrderSettlementType();
    os2.setDPrognosisMessageID(DPROGNOSIS_MESSAGE_ID2);
    FlexOrderSettlementType os3WithoutDPorgMsgId = new FlexOrderSettlementType();

    FlexSettlement flexSettlement = new FlexSettlement();
    flexSettlement.getFlexOrderSettlements().addAll(List.of(os1, os2, os3WithoutDPorgMsgId));

    return Stream.of(
        Arguments.of(flexOffer, List.of(DPROGNOSIS_MESSAGE_ID1)),
        Arguments.of(flexOrder, List.of(DPROGNOSIS_MESSAGE_ID1)),
        Arguments.of(flexSettlement, List.of(DPROGNOSIS_MESSAGE_ID1, DPROGNOSIS_MESSAGE_ID2)) // unique values are found
    );
  }

  @ParameterizedTest
  @MethodSource("withoutParameter")
  void valid_true_whenNoValueIsPresent(PayloadMessageType payloadMessage) {
    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, payloadMessage))).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_true_whenFoundMessageIdIsOfKnownMessage(PayloadMessageType payloadMessage, List<String> baselineRefs) {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);

    if (baselineRefs.size() >= 1) {
      given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(DPROGNOSIS_MESSAGE_ID1, DPrognosis.class))).willReturn(Optional.of(dPrognosisType1));
    }
    if (baselineRefs.size() == 2) {
      given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(DPROGNOSIS_MESSAGE_ID2, DPrognosis.class))).willReturn(Optional.of(dPrognosisType2));
    }

    assertThat(testSubject.valid(uftpMessage)).isTrue();
  }

  @ParameterizedTest
  @MethodSource("withParameter")
  void valid_false_whenFoundMessageIdIsOfUnknownMessage(PayloadMessageType payloadMessage, List<String> baselineRefs) {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, payloadMessage);

    given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(DPROGNOSIS_MESSAGE_ID1, DPrognosis.class))).willReturn(Optional.empty());

    assertThat(testSubject.valid(uftpMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference D-PrognosisMessageID");
  }
}