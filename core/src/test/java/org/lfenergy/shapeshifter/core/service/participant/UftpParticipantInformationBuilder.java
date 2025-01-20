package org.lfenergy.shapeshifter.core.service.participant;

import net.datafaker.Faker;
import org.lfenergy.shapeshifter.api.model.UftpParticipantInformation;

import java.util.Locale;
import java.util.Random;

public class UftpParticipantInformationBuilder {

    private static final Faker FAKER = new Faker(Locale.forLanguageTag("nl"));
    private static final Random RANDOM = new Random();

    private String domain;
    private String publicKey;
    private String endpoint;
    private boolean requiresAuthorization;

    public UftpParticipantInformationBuilder() {
        reset();
    }

    public UftpParticipantInformationBuilder withDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public UftpParticipantInformationBuilder withPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public UftpParticipantInformationBuilder withEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public UftpParticipantInformationBuilder withRequiresAuthorization(boolean requiresAuthorization) {
        this.requiresAuthorization = requiresAuthorization;
        return this;
    }

    public UftpParticipantInformation build() {
        try {
            return new UftpParticipantInformation(domain, publicKey, endpoint, requiresAuthorization);
        } finally {
            reset();
        }
    }

    private void reset() {
        domain = FAKER.internet().webdomain();
        publicKey = FAKER.regexify("[a-zA-Z0-9]{44}=");
        endpoint = "https://" + FAKER.internet().webdomain();
        requiresAuthorization = RANDOM.nextBoolean();
    }


}
