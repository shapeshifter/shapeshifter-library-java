package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexMessageType;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexOrderOptionReferenceValidatorTest {

  private static final String FLEX_OFFER_ID = "FLEX_OFFER_ID";
  private static final String OPTION_REFERENCE = "OPTION_REFERENCE";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private ReferencedFlexOrderOptionReferenceValidator testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private FlexOrder flexOrder;

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        support,
        sender,
        flexOrder
    );
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(PayloadMessageType.class)).isFalse();
    assertThat(testSubject.appliesTo(FlexMessageType.class)).isFalse();
    assertThat(testSubject.appliesTo(FlexOffer.class)).isFalse();
  }

  @Test
  void valid_true_whenNoValueIsPresent() {
    given(flexOrder.getOptionReference()).willReturn(null);

    assertThat(testSubject.valid(sender, flexOrder)).isTrue();
  }

  @Test
  void valid_true_whenFoundValueIsSupported() {
    given(flexOrder.getOptionReference()).willReturn(OPTION_REFERENCE);
    given(flexOrder.getFlexOfferMessageID()).willReturn(FLEX_OFFER_ID);

    given(support.isValidOfferOptionReference(FLEX_OFFER_ID, OPTION_REFERENCE)).willReturn(true);

    assertThat(testSubject.valid(sender, flexOrder)).isTrue();
  }

  @Test
  void valid_false_whenFoundValueIsNotSupported() {
    given(flexOrder.getOptionReference()).willReturn(OPTION_REFERENCE);
    given(flexOrder.getFlexOfferMessageID()).willReturn(FLEX_OFFER_ID);

    given(support.isValidOfferOptionReference(FLEX_OFFER_ID, OPTION_REFERENCE)).willReturn(false);

    assertThat(testSubject.valid(sender, flexOrder)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Unknown reference OptionReference in FlexOrder");
  }
}