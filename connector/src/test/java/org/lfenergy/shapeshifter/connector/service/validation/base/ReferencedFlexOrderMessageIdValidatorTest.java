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
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexOrderMessageIdValidatorTest {

  private static final String FLEX_OREDER_MESSAGE_ID1 = "FLEX_OREDER_MESSAGE_ID1";
  private static final String FLEX_OREDER_MESSAGE_ID2 = "FLEX_OREDER_MESSAGE_ID2";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private ReferencedFlexOrderMessageIdValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private DPrognosisResponse prognosisResponse;
  @Mock
  private FlexOrderStatusType status1, status2;
  @Mock
  private FlexOrder flexOrder1, flexOrder2;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        sender,
        prognosisResponse,
        status1, status2,
        flexOrder1, flexOrder2
    );
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
    given(prognosisResponse.getFlexOrderStatuses()).willReturn(List.of());

    assertThat(testSubject.valid(sender, prognosisResponse)).isTrue();
  }

  @Test
  void valid_whenAllReferencesInListAreKnow() {
    given(prognosisResponse.getFlexOrderStatuses()).willReturn(List.of(
        status1, status2
    ));
    given(status1.getFlexOrderMessageID()).willReturn(FLEX_OREDER_MESSAGE_ID1);
    given(status2.getFlexOrderMessageID()).willReturn(FLEX_OREDER_MESSAGE_ID2);
    given(support.getPreviousMessage(FLEX_OREDER_MESSAGE_ID1, FlexOrder.class)).willReturn(Optional.of(flexOrder1));
    given(support.getPreviousMessage(FLEX_OREDER_MESSAGE_ID2, FlexOrder.class)).willReturn(Optional.of(flexOrder2));

    assertThat(testSubject.valid(sender, prognosisResponse)).isTrue();
  }

  @Test
  void invalid_whenSingleReferenceInListIsUnknown() {
    given(prognosisResponse.getFlexOrderStatuses()).willReturn(List.of(
        status1, status2
    ));
    given(status1.getFlexOrderMessageID()).willReturn(FLEX_OREDER_MESSAGE_ID1);
    given(status2.getFlexOrderMessageID()).willReturn(FLEX_OREDER_MESSAGE_ID2);
    given(support.getPreviousMessage(FLEX_OREDER_MESSAGE_ID1, FlexOrder.class)).willReturn(Optional.of(flexOrder1));
    given(support.getPreviousMessage(FLEX_OREDER_MESSAGE_ID2, FlexOrder.class)).willReturn(Optional.empty());

    assertThat(testSubject.valid(sender, prognosisResponse)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference FlexOrderMessageID");
  }
}