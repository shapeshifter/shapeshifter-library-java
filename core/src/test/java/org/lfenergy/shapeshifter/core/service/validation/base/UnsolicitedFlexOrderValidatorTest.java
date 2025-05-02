// Copyright 2025 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.api.TestMessage;
import org.lfenergy.shapeshifter.api.USEFRoleType;
import org.lfenergy.shapeshifter.core.model.UftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UnsolicitedFlexOrderValidatorTest {

    private static final String FLEX_OFFER_MESSAGE_ID = "FLEX_OFFER_MESSAGE_ID";

    private final UftpParticipant sender = new UftpParticipant("dso.org", USEFRoleType.DSO);

    private final UnsolicitedFlexOrderValidator validator = new UnsolicitedFlexOrderValidator();

    @Test
    void appliesTo() {
        assertThat(validator.appliesTo(FlexOrder.class)).isTrue();
    }

    @Test
    void notAppliesTo() {
        // Not necessary to test with all types. Is tested on base class and by testing the map.
        assertThat(validator.appliesTo(TestMessage.class)).isFalse();
    }

    @Test
    void isValid_Unsolicited_null_FlexOfferMessageID_null() {
        var flexOrder = new FlexOrder();
        flexOrder.setUnsolicited(null);
        flexOrder.setFlexOfferMessageID(null);

        assertThat(validator.isValid(createUftpMessage(flexOrder))).isFalse();
    }

    @Test
    void isValid_backwards_compatibility_Unsolicited_null_FlexOfferMessageID_not_null() {
        var flexOrder = new FlexOrder();
        flexOrder.setUnsolicited(null);
        flexOrder.setFlexOfferMessageID(FLEX_OFFER_MESSAGE_ID);

        assertThat(validator.isValid(createUftpMessage(flexOrder))).isTrue();
    }

    @Test
    void isValid_Unsolicited_true_null_FlexOfferMessageID_null() {
        var flexOrder = new FlexOrder();
        flexOrder.setUnsolicited(true);
        flexOrder.setFlexOfferMessageID(null);

        assertThat(validator.isValid(createUftpMessage(flexOrder))).isTrue();
    }

    @Test
    void isValid_Unsolicited_true_FlexOfferMessageID_not_null() {
        var flexOrder = new FlexOrder();
        flexOrder.setUnsolicited(true);
        flexOrder.setFlexOfferMessageID(FLEX_OFFER_MESSAGE_ID);

        assertThat(validator.isValid(createUftpMessage(flexOrder))).isFalse();
    }

    @Test
    void isValid_Unsolicited_false_FlexOfferMessageID_null() {
        var flexOrder = new FlexOrder();
        flexOrder.setUnsolicited(false);
        flexOrder.setFlexOfferMessageID(null);

        assertThat(validator.isValid(createUftpMessage(flexOrder))).isFalse();
    }

    @Test
    void isValid_Unsolicited_false_FlexOfferMessageID_not_null() {
        var flexOrder = new FlexOrder();
        flexOrder.setUnsolicited(false);
        flexOrder.setFlexOfferMessageID(FLEX_OFFER_MESSAGE_ID);

        assertThat(validator.isValid(createUftpMessage(flexOrder))).isTrue();
    }

    private UftpMessage<FlexOrder> createUftpMessage(FlexOrder flexOrder) {
        return UftpMessage.createIncoming(sender, flexOrder, null, null);
    }

}