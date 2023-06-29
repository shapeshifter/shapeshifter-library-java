// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.participant.UftpParticipantFixture;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FlexOrderCurrencyValidatorTest {


    private static final UftpParticipant DSO = UftpParticipantFixture.createTestAGRParticipant();

    private final FlexOrderCurrencyValidator flexOrderCurrencyValidator = new FlexOrderCurrencyValidator();

    @Test
    void appliesTo() {
        assertThat(flexOrderCurrencyValidator.appliesTo(FlexOffer.class)).isFalse();
        assertThat(flexOrderCurrencyValidator.appliesTo(FlexOrder.class)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideIsValidArguments")
    void isValid(String currency, boolean expectedResult) {
        var flexOrder = new FlexOrder();
        flexOrder.setCurrency(currency);

        var result = flexOrderCurrencyValidator.isValid(UftpMessageFixture.createIncoming(DSO, flexOrder));

        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideIsValidArguments() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of(" ", false),
                Arguments.of("EURR", false),
                Arguments.of("123", false),
                Arguments.of("EUR ", false),
                Arguments.of("EUR", true)
        );
    }

}