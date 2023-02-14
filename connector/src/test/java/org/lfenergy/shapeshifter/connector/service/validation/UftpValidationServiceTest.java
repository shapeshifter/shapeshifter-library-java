package org.lfenergy.shapeshifter.connector.service.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferResponse;
import org.lfenergy.shapeshifter.connector.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpValidationServiceTest {


  @Mock
  private UftpBaseValidator<FlexOffer> baseValidation1;
  @Mock
  private UftpBaseValidator<FlexOffer> baseValidation2;
  @Mock
  private UftpMessageValidator<FlexOffer> messageValidation1;
  @Mock
  private UftpMessageValidator<FlexOffer> messageValidation2;
  @Mock
  private UftpUserDefinedValidator<FlexOffer> userDefinedValidation1;
  @Mock
  private UftpUserDefinedValidator<FlexOfferResponse> userDefinedValidation2;

  private UftpValidationService testSubject;

  @Mock
  private UftpParticipant sender;
  @Mock
  private FlexOffer flexOffer;
  @Mock
  private FlexOfferResponse flexOfferResponse;

  @BeforeEach
  void createTestSubject() {
    testSubject = new UftpValidationService(
        List.of(baseValidation1, baseValidation2),
        List.of(messageValidation1, messageValidation2),
        List.of(userDefinedValidation1, userDefinedValidation2)
    );
  }

  @AfterEach
  void noMore() {
    verifyNoMoreInteractions(
        baseValidation1,
        baseValidation2,
        messageValidation1,
        messageValidation2,
        userDefinedValidation1,
        userDefinedValidation2,
        sender,
        flexOffer,
        flexOfferResponse
    );
  }

  @Test
  void validate_multiple_apply_all_ok() {
    given(baseValidation1.appliesTo(FlexOffer.class)).willReturn(true);
    given(baseValidation2.appliesTo(FlexOffer.class)).willReturn(true);
    given(messageValidation1.appliesTo(FlexOffer.class)).willReturn(true);
    given(messageValidation2.appliesTo(FlexOffer.class)).willReturn(true);
    given(userDefinedValidation1.appliesTo(FlexOffer.class)).willReturn(true);
    given(userDefinedValidation2.appliesTo(FlexOffer.class)).willReturn(false);

    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);

    given(baseValidation1.valid(uftpMessage)).willReturn(true);
    given(baseValidation2.valid(uftpMessage)).willReturn(true);
    given(messageValidation1.valid(uftpMessage)).willReturn(true);
    given(messageValidation2.valid(uftpMessage)).willReturn(true);
    given(userDefinedValidation1.valid(uftpMessage)).willReturn(true);

    var result = testSubject.validate(uftpMessage);

    assertThat(result.valid()).isTrue();
    assertThat(result.rejectionReason()).isNull();
  }

  @Test
  void validate_one_applies_is_ok() {
    given(baseValidation1.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(baseValidation2.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(messageValidation1.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(messageValidation2.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(userDefinedValidation1.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(userDefinedValidation2.appliesTo(FlexOfferResponse.class)).willReturn(true);

    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOfferResponse);

    given(userDefinedValidation2.valid(uftpMessage)).willReturn(true);

    var result = testSubject.validate(uftpMessage);

    assertThat(result.valid()).isTrue();
    assertThat(result.rejectionReason()).isNull();
  }

  @Test
  void validate_none_apply() {
    given(baseValidation1.appliesTo(FlexOffer.class)).willReturn(false);
    given(baseValidation2.appliesTo(FlexOffer.class)).willReturn(false);
    given(messageValidation1.appliesTo(FlexOffer.class)).willReturn(false);
    given(messageValidation2.appliesTo(FlexOffer.class)).willReturn(false);
    given(userDefinedValidation1.appliesTo(FlexOffer.class)).willReturn(false);
    given(userDefinedValidation2.appliesTo(FlexOffer.class)).willReturn(false);

    var result = testSubject.validate(UftpMessageFixture.createOutgoing(sender, flexOffer));

    assertThat(result.valid()).isTrue();
    assertThat(result.rejectionReason()).isNull();
  }

  @Test
  void validate_multiple_apply_first_fails() {
    given(baseValidation1.appliesTo(FlexOffer.class)).willReturn(true);

    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);

    given(baseValidation1.valid(uftpMessage)).willReturn(false);
    given(baseValidation1.getReason()).willReturn("baseValidation1");

    var result = testSubject.validate(uftpMessage);

    assertThat(result.valid()).isFalse();
    assertThat(result.rejectionReason()).isEqualTo("baseValidation1");
  }

  @Test
  void validate_multiple_apply_third_fails() {
    given(baseValidation1.appliesTo(FlexOffer.class)).willReturn(true);
    given(baseValidation2.appliesTo(FlexOffer.class)).willReturn(true);
    given(messageValidation1.appliesTo(FlexOffer.class)).willReturn(true);

    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);

    given(baseValidation1.valid(uftpMessage)).willReturn(true);
    given(baseValidation2.valid(uftpMessage)).willReturn(true);
    given(messageValidation1.valid(uftpMessage)).willReturn(false);
    given(messageValidation1.getReason()).willReturn("messageValidation1");

    var result = testSubject.validate(uftpMessage);

    assertThat(result.valid()).isFalse();
    assertThat(result.rejectionReason()).isEqualTo("messageValidation1");
  }

  @Test
  void validate_multiple_apply_fifth_fails() {
    given(baseValidation1.appliesTo(FlexOffer.class)).willReturn(true);
    given(baseValidation2.appliesTo(FlexOffer.class)).willReturn(true);
    given(messageValidation1.appliesTo(FlexOffer.class)).willReturn(true);
    given(messageValidation2.appliesTo(FlexOffer.class)).willReturn(true);
    given(userDefinedValidation1.appliesTo(FlexOffer.class)).willReturn(true);

    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOffer);

    given(baseValidation1.valid(uftpMessage)).willReturn(true);
    given(baseValidation2.valid(uftpMessage)).willReturn(true);
    given(messageValidation1.valid(uftpMessage)).willReturn(true);
    given(messageValidation2.valid(uftpMessage)).willReturn(true);
    given(userDefinedValidation1.valid(uftpMessage)).willReturn(false);
    given(userDefinedValidation1.getReason()).willReturn("userDefinedValidation1");

    var result = testSubject.validate(uftpMessage);

    assertThat(result.valid()).isFalse();
    assertThat(result.rejectionReason()).isEqualTo("userDefinedValidation1");
  }

  @Test
  void validate_one_applies_fails() {
    given(baseValidation1.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(baseValidation2.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(messageValidation1.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(messageValidation2.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(userDefinedValidation1.appliesTo(FlexOfferResponse.class)).willReturn(false);
    given(userDefinedValidation2.appliesTo(FlexOfferResponse.class)).willReturn(true);

    var uftpMessage = UftpMessageFixture.createOutgoing(sender, flexOfferResponse);

    given(userDefinedValidation2.valid(uftpMessage)).willReturn(false);
    given(userDefinedValidation2.getReason()).willReturn("userDefinedValidation2");

    var result = testSubject.validate(uftpMessage);

    assertThat(result.valid()).isFalse();
    assertThat(result.rejectionReason()).isEqualTo("userDefinedValidation2");
  }
}