package org.lfenergy.shapeshifter.connector.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOffer;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOfferOptions;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexRequest;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.messageId;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActiveFlexRequestValidatorTest {

  @Mock
  private UftpParticipant sender;

  @Mock
  private UftpValidatorSupport uftpValidatorSupport;
  @InjectMocks
  private ActiveFlexRequestValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Flex Offer does not contain a reference to an active Flex Request");
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOffer.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(FlexOrder.class)).isFalse();
  }

  @Test
  void test_happy_flow_1_flex_request_reference_is_empty() {
    var flexOffer = flexOffer(null, flexOfferOptions());
    assertThat(testSubject.valid(sender, flexOffer)).isTrue();
    verifyNoInteractions(uftpValidatorSupport);
  }

  @Test
  void test_happy_flow_3_flex_request_active_in_the_future() {
    var flexMessageId = messageId();
    var flexRequest = flexRequest(flexMessageId, OffsetDateTime.now().plusDays(1));
    var flexOffer = flexOffer(flexMessageId, flexOfferOptions(BigDecimal.valueOf(1.0)));

    given(uftpValidatorSupport.getPreviousMessage(flexMessageId, FlexRequest.class)).willReturn(Optional.of(flexRequest));
    assertThat(testSubject.valid(sender, flexOffer)).isTrue();
  }

  @Test
  void test_flex_request_reference_present_request_not_found() {
    var flexRequestId = UUID.randomUUID().toString();
    var flexOffer = flexOffer(flexRequestId, flexOfferOptions());
    given(uftpValidatorSupport.getPreviousMessage(flexRequestId, FlexRequest.class)).willReturn(Optional.empty());
    assertThat(testSubject.valid(sender, flexOffer)).isFalse();
  }

  @Test
  void test_flex_request_in_no_longer_active_in_the_past() {
    var flexMessageId = messageId();
    var flexRequest = flexRequest(flexMessageId, OffsetDateTime.now().minusDays(1));
    var flexOffer = flexOffer(flexMessageId, flexOfferOptions(BigDecimal.valueOf(1)));

    given(uftpValidatorSupport.getPreviousMessage(flexMessageId, FlexRequest.class)).willReturn(Optional.of(flexRequest));
    assertThat(testSubject.valid(sender, flexOffer)).isFalse();
  }
}
