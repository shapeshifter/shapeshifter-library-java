package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.DPrognosisResponse;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexOrderStatusType;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexOrderMessageIdValidatorTest {

  private static final String FLEX_ORDER_MESSAGE_ID1 = "FLEX_ORDER_MESSAGE_ID1";
  private static final String FLEX_ORDER_MESSAGE_ID2 = "FLEX_ORDER_MESSAGE_ID2";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private ReferencedFlexOrderMessageIdValidator testSubject;

  private final UftpParticipant sender = new UftpParticipant("example.com", USEFRoleType.DSO);
  private final DPrognosisResponse prognosisResponse = new DPrognosisResponse();
  private final FlexOrderStatusType status1 = new FlexOrderStatusType();
  private final FlexOrderStatusType status2 = new FlexOrderStatusType();
  private final FlexOrder flexOrder1 = new FlexOrder();
  private final FlexOrder flexOrder2 = new FlexOrder();

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(support);
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(DPrognosisResponse.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessageResponse.class)).isFalse();
  }

  @Test
  void valid_whenNoReferencesInResponse() {
    prognosisResponse.getFlexOrderStatuses().clear();

    assertThat(testSubject.valid(UftpMessageFixture.createOutgoing(sender, prognosisResponse))).isTrue();
  }

  @Test
  void valid_whenAllReferencesInListAreKnow() {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, prognosisResponse);

    prognosisResponse.getFlexOrderStatuses().addAll(List.of(status1, status2));
    status1.setFlexOrderMessageID(FLEX_ORDER_MESSAGE_ID1);
    status2.setFlexOrderMessageID(FLEX_ORDER_MESSAGE_ID2);
    given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(FLEX_ORDER_MESSAGE_ID1, FlexOrder.class))).willReturn(Optional.of(flexOrder1));
    given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(FLEX_ORDER_MESSAGE_ID2, FlexOrder.class))).willReturn(Optional.of(flexOrder2));

    assertThat(testSubject.valid(uftpMessage)).isTrue();
  }

  @Test
  void invalid_whenSingleReferenceInListIsUnknown() {
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, prognosisResponse);

    prognosisResponse.getFlexOrderStatuses().addAll(List.of(status1, status2));
    status1.setFlexOrderMessageID(FLEX_ORDER_MESSAGE_ID1);
    status2.setFlexOrderMessageID(FLEX_ORDER_MESSAGE_ID2);
    given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(FLEX_ORDER_MESSAGE_ID1, FlexOrder.class))).willReturn(Optional.of(flexOrder1));
    given(support.getPreviousMessage(uftpMessage.referenceToPreviousMessage(FLEX_ORDER_MESSAGE_ID2, FlexOrder.class))).willReturn(Optional.empty());

    assertThat(testSubject.valid(uftpMessage)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference FlexOrderMessageID");
  }
}