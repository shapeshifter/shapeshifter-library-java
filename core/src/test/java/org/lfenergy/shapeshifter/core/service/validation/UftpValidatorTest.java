package org.lfenergy.shapeshifter.core.service.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UftpValidatorTest {

  private static final String REJECTION_REASON = "Invalid message";

  @Mock
  private UftpValidator<FlexOffer> validator;

  private final UftpMessage<FlexOffer> uftpMessage = UftpMessage.createOutgoing(
      new UftpParticipant("sender", USEFRoleType.DSO),
      new FlexOffer()
  );

  @BeforeEach
  void useDefaultValidateImplementation() {
    doCallRealMethod().when(validator).validate(any());
  }

  @Test
  void validate_validMessage_returnsOk() {
    given(validator.isValid(uftpMessage)).willReturn(true);

    var result = validator.validate(uftpMessage);

    assertThat(result).isEqualTo(ValidationResult.ok());
    verify(validator).isValid(uftpMessage);
    verify(validator, never()).getReason();
  }

  @Test
  void validate_invalidMessage_returnsRejectionWithReason() {
    given(validator.isValid(uftpMessage)).willReturn(false);
    given(validator.getReason()).willReturn(REJECTION_REASON);

    var result = validator.validate(uftpMessage);

    assertThat(result).isEqualTo(ValidationResult.rejection(REJECTION_REASON));
    verify(validator).isValid(uftpMessage);
    verify(validator).getReason();
  }

  @Test
  void validate_invalidMessageWithBlankReason_throwsIllegalArgumentException() {
    given(validator.isValid(uftpMessage)).willReturn(false);
    given(validator.getReason()).willReturn("  ");

    assertThatThrownBy(() -> validator.validate(uftpMessage))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Rejection reason cannot be blank.");

    verify(validator).isValid(uftpMessage);
    verify(validator).getReason();
  }

  @Test
  void validate_invalidMessageWithNullReason_throwsIllegalArgumentException() {
    given(validator.isValid(uftpMessage)).willReturn(false);
    given(validator.getReason()).willReturn(null);

    assertThatThrownBy(() -> validator.validate(uftpMessage))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Rejection reason cannot be blank.");

    verify(validator).isValid(uftpMessage);
    verify(validator).getReason();
  }
}
