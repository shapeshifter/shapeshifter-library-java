package org.lfenergy.shapeshifter.connector.service.validation;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.connector.model.UftpMessage;
import org.lfenergy.shapeshifter.connector.service.validation.model.ValidationResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UftpValidationService {

  private final List<UftpBaseValidator<? extends PayloadMessageType>> baseValidations;
  private final List<UftpMessageValidator<? extends PayloadMessageType>> messageValidations;
  private final List<UftpUserDefinedValidator<? extends PayloadMessageType>> userDefinedValidations;

  public ValidationResult validate(UftpMessage<? extends PayloadMessageType> uftpMessage) {
    log.debug("Validating received {} message", uftpMessage.payloadMessage().getClass());
    return Stream.of(baseValidations, messageValidations, userDefinedValidations)
                 .flatMap(List::stream)
                 .map(validator -> validate(validator, uftpMessage))
                 .filter(r -> !r.valid())
                 .findFirst()
                 .orElseGet(ValidationResult::ok);
  }

  @SuppressWarnings("unchecked")
  // There is an unchecked cast to type T. However, this has already been verified by the appliesTo method of the validator.
  private <T extends PayloadMessageType> ValidationResult validate(UftpValidator<T> validator, UftpMessage<? extends PayloadMessageType> uftpMessage) {
    if (validator.appliesTo(uftpMessage.payloadMessage().getClass()) && !validator.valid((UftpMessage<T>) uftpMessage)) {
      return ValidationResult.rejection(validator.getReason());
    }
    return ValidationResult.ok();
  }
}
