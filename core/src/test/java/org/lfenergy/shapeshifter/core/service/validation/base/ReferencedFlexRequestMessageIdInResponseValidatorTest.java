package org.lfenergy.shapeshifter.core.service.validation.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.PayloadMessageResponseType;
import org.lfenergy.shapeshifter.api.TestMessageResponse;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReferencedFlexRequestMessageIdInResponseValidatorTest {
    private static final String FLEX_REQUEST_MESSAGE_ID = UUID.randomUUID().toString();
    private static final String CONVERSATION_ID = UUID.randomUUID().toString();
    @Mock
    private UftpMessageSupport messageSupport;

    @InjectMocks
    private ReferencedFlexRequestMessageIdInResponseValidator testSubject;

    private final UftpParticipant sender = new UftpParticipant("example.com", USEFRoleType.DSO);
    private final PayloadMessageResponseType flexRequestResponse = new FlexRequestResponse();
    private final FlexRequest flexRequest = new FlexRequest();

    @Test
    void appliesTo() {
        assertThat(testSubject.appliesTo(FlexRequestResponse.class)).isTrue();
    }

    @Test
    void notAppliesTo() {
        assertThat(testSubject.appliesTo(TestMessageResponse.class)).isFalse();
    }

    @Test
    void valid_whenNoFlexRequestReferenceInResponse() {
        ((FlexRequestResponse) flexRequestResponse).setFlexRequestMessageID(null);

        assertThat(testSubject.isValid(UftpMessageFixture.createIncomingResponse(sender, flexRequestResponse))).isFalse();
    }

    @Test
    void valid_whenFlexRequestReferenceInResponseIsKnown() {
        var uftpMessage = UftpMessageFixture.createIncomingResponse(sender, flexRequestResponse);

        ((FlexRequestResponse) flexRequestResponse).setFlexRequestMessageID(FLEX_REQUEST_MESSAGE_ID);
        flexRequestResponse.setConversationID(CONVERSATION_ID);
        given(messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(FLEX_REQUEST_MESSAGE_ID, CONVERSATION_ID,
                FlexRequest.class))).willReturn(Optional.of(flexRequest));

        assertThat(testSubject.isValid(uftpMessage)).isTrue();
    }

    @Test
    void invalid_whenReferenceInResponseIsNotKnown() {
        var uftpMessage = UftpMessageFixture.createIncomingResponse(sender, flexRequestResponse);

        ((FlexRequestResponse) flexRequestResponse).setFlexRequestMessageID(FLEX_REQUEST_MESSAGE_ID);
        flexRequestResponse.setConversationID(CONVERSATION_ID);
        given(messageSupport.findReferencedMessage(uftpMessage.referenceToPreviousMessage(FLEX_REQUEST_MESSAGE_ID, CONVERSATION_ID,
                FlexRequest.class))).willReturn(Optional.empty());

        assertThat(testSubject.isValid(uftpMessage)).isFalse();
    }

    @Test
    void getReason() {
        assertThat(testSubject.getReason()).isEqualTo("Unknown reference Request message ID");
    }
}
