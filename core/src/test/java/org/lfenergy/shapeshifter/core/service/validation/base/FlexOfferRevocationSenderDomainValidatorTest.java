// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferRevocation;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpMessageReference;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.core.service.validation.base.TestDataHelper.conversationId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class FlexOfferRevocationSenderDomainValidatorTest {

    private static final String MATCHING_NESSAGE_ID = "MATCHING_MESSAGE_ID";
    private static final String SENDER_DOMAIN = "SENDER_DOMAIN";
    private static final String ANOTHER_SENDER_DOMAIN = "ANOTHER_SENDER_DOMAIN";
    private static final String RECIPIENT_DOMAIN = "RECIPIENT_DOMAIN";
    private static final String CONVERSATION_ID = conversationId();
    @Mock
    private UftpMessageSupport messageSupport;

    @InjectMocks
    private FlexOfferRevocationSenderDomainValidator testSubject;

    @Mock
    private UftpParticipant sender;
    @Mock
    private FlexOffer flexOffer;
    @Mock
    private FlexOfferRevocation flexOfferRevocation;

    @AfterEach
    void noMore() {
        verifyNoMoreInteractions(
                messageSupport,
                sender,
                flexOfferRevocation
        );
    }

    @Test
    void appliesTo() {
        assertThat(testSubject.appliesTo(FlexOfferRevocation.class)).isTrue();
    }

    @Test
    void notAppliesTo() {
        assertThat(testSubject.appliesTo(TestMessage.class)).isFalse();
    }

    @Test
    void valid_whenSenderDomainsMatch() {
        given(flexOffer.getSenderDomain()).willReturn(SENDER_DOMAIN);

        given(flexOfferRevocation.getFlexOfferMessageID()).willReturn(MATCHING_NESSAGE_ID);
        given(flexOfferRevocation.getConversationID()).willReturn(CONVERSATION_ID);
        given(flexOfferRevocation.getSenderDomain()).willReturn(SENDER_DOMAIN);
        given(flexOfferRevocation.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);

        given(messageSupport.findReferencedMessage(any(UftpMessageReference.class))).willReturn(Optional.of(flexOffer));

        assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOfferRevocation))).isTrue();
    }

    @Test
    void valid_whenSenderDomainsDoNotMatch() {
        given(flexOffer.getSenderDomain()).willReturn(SENDER_DOMAIN);

        given(flexOfferRevocation.getFlexOfferMessageID()).willReturn(MATCHING_NESSAGE_ID);
        given(flexOfferRevocation.getConversationID()).willReturn(CONVERSATION_ID);
        given(flexOfferRevocation.getSenderDomain()).willReturn(ANOTHER_SENDER_DOMAIN);
        given(flexOfferRevocation.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);

        given(messageSupport.findReferencedMessage(any(UftpMessageReference.class))).willReturn(Optional.of(flexOffer));

        assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOfferRevocation))).isFalse();
    }

    @Test
    void valid_whenFlexOfferDoesNotExist() {
        given(flexOfferRevocation.getFlexOfferMessageID()).willReturn(MATCHING_NESSAGE_ID);
        given(flexOfferRevocation.getConversationID()).willReturn(CONVERSATION_ID);
        given(flexOfferRevocation.getSenderDomain()).willReturn(ANOTHER_SENDER_DOMAIN);
        given(flexOfferRevocation.getRecipientDomain()).willReturn(RECIPIENT_DOMAIN);

        given(messageSupport.findReferencedMessage(any(UftpMessageReference.class))).willReturn(Optional.empty());

        assertThat(testSubject.isValid(UftpMessageFixture.createOutgoing(sender, flexOfferRevocation))).isFalse();
    }

    @Test
    void getReason() {
        assertThat(testSubject.getReason()).isEqualTo("Flex Offer revocation can only be sent by the same Sender Domain that sent the Flex Offer");
    }
}