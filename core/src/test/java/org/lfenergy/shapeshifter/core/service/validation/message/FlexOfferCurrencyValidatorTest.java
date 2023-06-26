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

class FlexOfferCurrencyValidatorTest {

    private static final UftpParticipant DSO = UftpParticipantFixture.createTestAGRParticipant();

    private final FlexOfferCurrencyValidator flexOfferCurrencyValidator = new FlexOfferCurrencyValidator();

    @Test
    void appliesTo() {
        assertThat(flexOfferCurrencyValidator.appliesTo(FlexOffer.class)).isTrue();
        assertThat(flexOfferCurrencyValidator.appliesTo(FlexOrder.class)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideIsValidArguments")
    void isValid(String currency, boolean expectedResult) {
        var flexOffer = new FlexOffer();
        flexOffer.setCurrency(currency);

        var result = flexOfferCurrencyValidator.isValid(UftpMessageFixture.createIncoming(DSO, flexOffer));

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