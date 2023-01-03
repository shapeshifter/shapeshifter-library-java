package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexRequestMessageIdValidatorTest {

  private static final String FLEX_REQUEST_MESSAGE_ID = "FLEX_REQUEST_MESSAGE_ID";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private ReferencedFlexRequestMessageIdValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private FlexOffer flexOffer;
  @Mock
  private FlexRequest flexRequest;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        sender,
        flexOffer,
        flexRequest
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(TestMessageResponse.class)).isFalse();
  }

  @Test
  void valid_whenNoReferenceInRequest() {
    given(flexOffer.getFlexRequestMessageID()).willReturn(null);

    assertThat(testSubject.valid(sender, flexOffer)).isTrue();
  }

  @Test
  void valid_whenReferenceInRequestIsKnown() {
    given(flexOffer.getFlexRequestMessageID()).willReturn(FLEX_REQUEST_MESSAGE_ID);
    given(support.getPreviousMessage(FLEX_REQUEST_MESSAGE_ID, FlexRequest.class)).willReturn(Optional.of(flexRequest));

    assertThat(testSubject.valid(sender, flexOffer)).isTrue();
  }

  @Test
  void invalid_whenReferenceInRequestIsNotKnown() {
    given(flexOffer.getFlexRequestMessageID()).willReturn(FLEX_REQUEST_MESSAGE_ID);
    given(support.getPreviousMessage(FLEX_REQUEST_MESSAGE_ID, FlexRequest.class)).willReturn(Optional.empty());

    assertThat(testSubject.valid(sender, flexOffer)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference FlexRequestMessageID");
  }
}