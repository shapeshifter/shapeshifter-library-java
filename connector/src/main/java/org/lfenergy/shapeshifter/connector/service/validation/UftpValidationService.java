package org.lfenergy.shapeshifter.connector.service.validation;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpParticipant;
import org.lfenergy.shapeshifter.connector.service.validation.model.ValidationResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UftpValidationService {

  private final List<UftpBaseValidator<? extends PayloadMessageType>> baseValidations;
  private final List<UftpMessageValidator<? extends PayloadMessageType>> messageValidations;
  private final List<UftpUserDefinedValidator<? extends PayloadMessageType>> userDefinedValidations;

  public ValidationResult validate(UftpParticipant sender, PayloadMessageType payloadMessage) {
    log.debug("Validating received {} message", payloadMessage.getClass());
    return Stream.of(baseValidations, messageValidations, userDefinedValidations)
                 .flatMap(List::stream)
                 .map(validator -> validate(validator, sender, payloadMessage))
                 .filter(r -> !r.valid())
                 .findFirst()
                 .orElseGet(ValidationResult::ok);
  }

  @SuppressWarnings("unchecked")
  // There is an unchecked cast to type T. However, this has already been verified by the appliesTo method of the validator.
  private <T extends PayloadMessageType> ValidationResult validate(UftpValidator<T> validator, UftpParticipant sender, PayloadMessageType payloadMessage) {
    if (validator.appliesTo(payloadMessage.getClass())) {
      if (!validator.valid(sender, (T) payloadMessage)) {
        return ValidationResult.rejection(validator.getReason());
      }
    }
    return ValidationResult.ok();
  }
}
