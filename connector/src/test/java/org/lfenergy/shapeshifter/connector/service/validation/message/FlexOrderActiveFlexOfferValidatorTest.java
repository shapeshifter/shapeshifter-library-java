package org.lfenergy.shapeshifter.connector.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOffer;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOfferOptions;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOrder;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.messageId;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
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
class FlexOrderActiveFlexOfferValidatorTest {

  @Mock
  private UftpParticipant sender;

  @Mock
  private UftpValidatorSupport uftpValidatorSupport;

  @InjectMocks
  private FlexOrderActiveFlexOfferValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Flex Order is not related to an active Flex Offer");
  }

  @Test
  void appliesTo() {
    assertThat(testSubject.appliesTo(FlexOrder.class)).isTrue();
  }

  @Test
  void notAppliesTo() {
    assertThat(testSubject.appliesTo(FlexRequest.class)).isFalse();
  }

  @Test
  void test_happy_flow_flex_offer_references_active_flex_offer() {
    var flexOfferMessageId = messageId();
    var flexOrder = flexOrder(flexOfferMessageId);
    var flexOffer = flexOffer();
    given(uftpValidatorSupport.getPreviousMessage(flexOfferMessageId, FlexOffer.class)).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.valid(sender, flexOrder)).isTrue();
  }

  @Test
  void test_no_flex_offer_reference() {
    var flexOrder = flexOrder(null);
    assertThat(testSubject.valid(sender, flexOrder)).isFalse();
    verifyNoInteractions(uftpValidatorSupport);
  }

  @Test
  void test_flex_offer_message_id_does_not_return_a_flex_offer() {
    var flexOfferMessageId = messageId();
    var flexOrder = flexOrder(flexOfferMessageId);
    given(uftpValidatorSupport.getPreviousMessage(flexOfferMessageId, FlexOffer.class)).willReturn(Optional.empty());
    assertThat(testSubject.valid(sender, flexOrder)).isFalse();
  }

  @Test
  void test_flex_offer_is_no_longer_active() {
    var flexOfferMessageId = messageId();
    var flexOrder = flexOrder(flexOfferMessageId);
    var flexOffer = flexOffer(flexOfferMessageId, flexOfferOptions(BigDecimal.valueOf(1.0)), OffsetDateTime.now().minusDays(1));
    given(uftpValidatorSupport.getPreviousMessage(flexOfferMessageId, FlexOffer.class)).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.valid(sender, flexOrder)).isFalse();
  }
}