// Copyright 2025 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnsolicitedFlexOfferValidatorTest {

    private static final String FLEX_REQUEST_MESSAGE_ID = "FLEX_REQUEST_MESSAGE_ID";

    private final UftpParticipant sender = new UftpParticipant("agr.org", USEFRoleType.AGR);

    private final UnsolicitedFlexOfferValidator validator = new UnsolicitedFlexOfferValidator();

    @Test
    void appliesTo() {
        assertThat(validator.appliesTo(FlexOffer.class)).isTrue();
    }

    @Test
    void notAppliesTo() {
        // Not necessary to test with all types. Is tested on base class and by testing the map.
        assertThat(validator.appliesTo(TestMessage.class)).isFalse();
    }

    @Test
    void isValid_Unsolicited_null_FlexRequestMessageID_null() {
        var flexOffer = new FlexOffer();
        flexOffer.setUnsolicited(null);
        flexOffer.setFlexRequestMessageID(null);

        assertThat(validator.isValid(createUftpMessage(flexOffer))).isTrue();
    }

    @Test
    void isValid_Unsolicited_null_FlexRequestMessageID_not_null() {
        var flexOffer = new FlexOffer();
        flexOffer.setUnsolicited(null);
        flexOffer.setFlexRequestMessageID(FLEX_REQUEST_MESSAGE_ID);

        assertThat(validator.isValid(createUftpMessage(flexOffer))).isTrue();
    }

    @Test
    void isValid_Unsolicited_true_null_FlexRequestMessageID_null() {
        var flexOffer = new FlexOffer();
        flexOffer.setUnsolicited(true);
        flexOffer.setFlexRequestMessageID(null);

        assertThat(validator.isValid(createUftpMessage(flexOffer))).isTrue();
    }

    @Test
    void isValid_Unsolicited_true_FlexRequestMessageID_not_null() {
        var flexOffer = new FlexOffer();
        flexOffer.setUnsolicited(true);
        flexOffer.setFlexRequestMessageID(FLEX_REQUEST_MESSAGE_ID);

        assertThat(validator.isValid(createUftpMessage(flexOffer))).isFalse();
    }

    @Test
    void isValid_Unsolicited_false_FlexRequestMessageID_null() {
        var flexOffer = new FlexOffer();
        flexOffer.setUnsolicited(false);
        flexOffer.setFlexRequestMessageID(null);

        assertThat(validator.isValid(createUftpMessage(flexOffer))).isFalse();
    }

    @Test
    void isValid_Unsolicited_false_FlexRequestMessageID_not_null() {
        var flexOffer = new FlexOffer();
        flexOffer.setUnsolicited(false);
        flexOffer.setFlexRequestMessageID(FLEX_REQUEST_MESSAGE_ID);

        assertThat(validator.isValid(createUftpMessage(flexOffer))).isTrue();
    }

    private UftpMessage<FlexOffer> createUftpMessage(FlexOffer flexOffer) {
        return UftpMessage.createIncoming(sender, flexOffer, null, null);
    }

}