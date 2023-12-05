package org.lfenergy.shapeshifter.core.service.validation.base;


import lombok.RequiredArgsConstructor;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpRequestResponseMapping;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidator;


@RequiredArgsConstructor
public class ReferencedFlexRequestMessageIdInResponseValidator implements UftpValidator<FlexRequestResponse> {

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
    public boolean isValid(UftpMessage<FlexRequestResponse> message) {
        var value = message.payloadMessage().getFlexRequestMessageID();
        return value != null && support.findReferencedMessage(message.referenceToPreviousMessage(value, message.payloadMessage().getConversationID(),
                FlexRequest.class)).isPresent();
    }

    @Override
    public String getReason() {
        return "Unknown reference Request message ID";
    }
}
