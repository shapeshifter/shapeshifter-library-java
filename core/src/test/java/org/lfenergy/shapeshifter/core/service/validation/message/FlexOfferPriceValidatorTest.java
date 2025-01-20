// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.service.validation.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.lfenergy.shapeshifter.api.FlexOffer;
import org.lfenergy.shapeshifter.api.FlexOfferOptionType;
import org.lfenergy.shapeshifter.api.FlexOrder;
import org.lfenergy.shapeshifter.core.model.UftpMessageFixture;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.participant.UftpParticipantFixture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FlexOfferPriceValidatorTest {

    private static final UftpParticipant DSO = UftpParticipantFixture.AGR;

    private final FlexOfferPriceValidator flexOfferPriceValidator = new FlexOfferPriceValidator();

    @Test
    void appliesTo() {
        assertThat(flexOfferPriceValidator.appliesTo(FlexOffer.class)).isTrue();
        assertThat(flexOfferPriceValidator.appliesTo(FlexOrder.class)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideIsValidArguments")
    void isValid(String currency, BigDecimal price, boolean expectedResult) {
        var flexOffer = new FlexOffer();
        flexOffer.setCurrency(currency);

        var offerOption = new FlexOfferOptionType();
        offerOption.setPrice(price);
        flexOffer.getOfferOptions().add(offerOption);

        var result = flexOfferPriceValidator.isValid(UftpMessageFixture.createIncoming(DSO, flexOffer));

        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideIsValidArguments() {
        return Stream.of(
                Arguments.of(null, BigDecimal.ZERO, false),
                Arguments.of(null, null, false),
                Arguments.of("", BigDecimal.ZERO, false),
                Arguments.of(" ", BigDecimal.ZERO, false),
                Arguments.of("EURR", BigDecimal.ZERO, false),
                Arguments.of("123", BigDecimal.ZERO, false),
                Arguments.of("EUR ", BigDecimal.ZERO, false),
                Arguments.of("EUR", null, true),
                Arguments.of("EUR", BigDecimal.ZERO, false),
                Arguments.of("EUR", BigDecimal.ZERO.setScale(1, RoundingMode.CEILING), false),
                Arguments.of("EUR", BigDecimal.ZERO.setScale(2, RoundingMode.CEILING), true),
                Arguments.of("EUR", BigDecimal.ZERO.setScale(3, RoundingMode.CEILING), false),
                Arguments.of("JPY", BigDecimal.ZERO.setScale(2, RoundingMode.CEILING), false),
                Arguments.of("JPY", BigDecimal.ZERO.setScale(1, RoundingMode.CEILING), false),
                Arguments.of("JPY", BigDecimal.ZERO, true)
        );
    }

}