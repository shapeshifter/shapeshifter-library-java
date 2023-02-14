package org.lfenergy.shapeshifter.connector.service.validation.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOffer;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOfferOption;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOfferOptionIsps;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.flexOrder;
import static org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper.messageId;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.connector.service.validation.base.TestDataHelper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlexOrderPriceMatchValidatorTest {

  @Mock
  private UftpParticipant sender;

  @Mock
  private UftpValidatorSupport uftpValidatorSupport;
  @InjectMocks
  private FlexOrderPriceMatchValidator testSubject;

  @Test
  void getReason() {
    assertThat(testSubject.getReason()).isEqualTo("Price in the order does not match the price given in the offer");
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
  void test_happy_flow_1_order_has_same_price_as_offer() {
    var flexMessageId = messageId();
    var flexOrder = flexOrder(flexMessageId);
    var flexOffer = flexOffer(flexMessageId,
                              List.of(TestDataHelper.flexOfferOption(flexOfferOptionIsps())));
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(uftpValidatorSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.valid(uftpMessage)).isTrue();
  }

  @Test
  void test_flex_order_has_different_price_than_options_in_offer() {
    var flexMessageId = messageId();
    var flexOrder = flexOrder(flexMessageId);
    var flexOffer = flexOffer(flexMessageId,
                              List.of(flexOfferOption(BigDecimal.valueOf(50.),
                                                      flexOfferOptionIsps())));
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);

    given(uftpValidatorSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(flexMessageId, FlexOffer.class))).willReturn(Optional.of(flexOffer));
    assertThat(testSubject.valid(uftpMessage)).isFalse();
  }

  @Test
  void test_flex_order_has_no_offer_referring_to_existing_offer_then_fail() {
    var messageId = messageId();
    var flexOrder = flexOrder(messageId);
    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOrder);
    given(uftpValidatorSupport.getPreviousMessage(uftpMessage.referenceToPreviousMessage(messageId, FlexOffer.class))).willReturn(Optional.empty());
    assertThat(testSubject.valid(uftpMessage)).isFalse();
  }
}
