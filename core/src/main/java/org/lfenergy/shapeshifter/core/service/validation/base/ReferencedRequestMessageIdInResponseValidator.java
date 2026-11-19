package org.lfenergy.shapeshifter.core.service.validation.base;

import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpRequestResponseMapping;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;
import org.lfenergy.shapeshifter.core.service.validation.model.ValidationResult;

@RequiredArgsConstructor
public class ReferencedRequestMessageIdInResponseValidator implements UftpValidator<PayloadMessageResponseType> {

    private static final String REJECTION_REASON = "Unknown reference Request message ID";

    private final UftpMessageSupport support;

    @Override
    public boolean appliesTo(Class<? extends PayloadMessageType> clazz) {
        return UftpRequestResponseMapping.hasReferencedRequestMessageId(clazz);
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public ValidationResult validate(UftpMessage<PayloadMessageResponseType> message) {
      return ValidationResult.of(isMessageValid(message), REJECTION_REASON);
    }

    @Override
    public boolean isValid(UftpMessage<PayloadMessageResponseType> message) {
      return isMessageValid(message);
    }

    @Override
    public String getReason() {
      return REJECTION_REASON;
    }

    private boolean isMessageValid(UftpMessage<PayloadMessageResponseType> message) {
        var value = UftpRequestResponseMapping.getReferencedRequestMessageId(message.payloadMessage());
        return value.isPresent() && support.findReferencedMessage(
                message.referenceToPreviousMessage(
                        value.get(),
                        message.payloadMessage().getConversationID(),
                        UftpRequestResponseMapping.getRequestTypeFor(message.payloadMessage())
                )
        ).isPresent();
    }

}
