package org.lfenergy.shapeshifter.connector.service.validation.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlexOrderOfferIsNotRevokedValidatorTest {

  private static final String MATCHING_NESSAGE_ID = "MATCHING_MESSAGE_ID";
  private static final String RECIPIENT_DOMAIN = "RECIPIENT_DOMAIN";

  @Mock
  private UftpValidatorSupport support;

  @InjectMocks
  private FlexOrderOfferIsNotRevokedValidator testSubject;

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
    assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
  }

  @Test
  void valid_whenNotRevoked() {
    given(flexOrder.getFlexOfferMessageID()).willReturn(MATCHING_NESSAGE_ID);
    given(flexOrder.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);

    given(support.existsFlexRevocation(MATCHING_NESSAGE_ID, RECIPIENT_DOMAIN)).willReturn(false);

    assertThat(testSubject.valid(sender, flexOrder)).isTrue();
  }

  @Test
  void invalid_whenRevoked() {
    given(flexOrder.getFlexOfferMessageID()).willReturn(MATCHING_NESSAGE_ID);
    given(flexOrder.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);

    given(support.existsFlexRevocation(MATCHING_NESSAGE_ID, RECIPIENT_DOMAIN)).willReturn(true);

    assertThat(testSubject.valid(sender, flexOrder)).isFalse();
  }

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Reference message revoked");
  }
}