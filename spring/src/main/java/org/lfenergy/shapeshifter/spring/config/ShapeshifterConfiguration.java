// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.config;

import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.core.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.core.common.xsd.XsdFactory;
import org.lfenergy.shapeshifter.core.common.xsd.XsdSchemaFactoryPool;
import org.lfenergy.shapeshifter.core.common.xsd.XsdSchemaProvider;
import org.lfenergy.shapeshifter.core.common.xsd.XsdValidator;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.ParticipantAuthorizationProvider;
import org.lfenergy.shapeshifter.core.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.core.service.UftpParticipantService;
import org.lfenergy.shapeshifter.core.service.crypto.LazySodiumBase64Pool;
import org.lfenergy.shapeshifter.core.service.crypto.LazySodiumFactory;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.handler.UftpPayloadHandler;
import org.lfenergy.shapeshifter.core.service.participant.ParticipantResolutionService;
import org.lfenergy.shapeshifter.core.service.receiving.DuplicateMessageDetection;
import org.lfenergy.shapeshifter.core.service.receiving.ReceivedMessageProcessor;
import org.lfenergy.shapeshifter.core.service.receiving.UftpReceivedMessageService;
import org.lfenergy.shapeshifter.core.service.sending.UftpSendMessageService;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.core.service.validation.*;
import org.lfenergy.shapeshifter.core.service.validation.base.*;
import org.lfenergy.shapeshifter.core.service.validation.message.*;
import org.lfenergy.shapeshifter.spring.ssl.SSLContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.net.ssl.SSLContext;
import java.net.http.HttpClient;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ComponentScan(value = "org.lfenergy.shapeshifter")
@EnableConfigurationProperties(ShapeshifterProperties.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({WebMvcConfigurer.class})
@CommonsLog
public class ShapeshifterConfiguration {

    private final ShapeshifterProperties properties;

    @Autowired
    public ShapeshifterConfiguration(ShapeshifterProperties properties) {
        this.properties = properties;
    }

    @ConditionalOnMissingBean
    @Bean
    public UftpSendMessageService uftpSendMessageService(UftpSerializer serializer,
                                                         UftpCryptoService cryptoService,
                                                         ParticipantResolutionService participantService,
                                                         ParticipantAuthorizationProvider participantAuthorizationProvider,
                                                         UftpValidationService uftpValidationService) {
        return new UftpSendMessageService(serializer, cryptoService, participantService, participantAuthorizationProvider, uftpValidationService, httpClient());
    }

    @ConditionalOnMissingBean
    @Bean
    public UftpCryptoService uftpCryptoService(ParticipantResolutionService participantService,
                                               LazySodiumFactory factory,
                                               LazySodiumBase64Pool lazySodiumInstancePool) {
        return new UftpCryptoService(participantService, factory, lazySodiumInstancePool);
    }

    @ConditionalOnMissingBean
    @Bean
    public LazySodiumFactory lazySodiumFactory() {
        return new LazySodiumFactory();
    }

    @ConditionalOnMissingBean
    @Bean
    public ReceivedMessageProcessor receivedMessageProcessor(UftpPayloadHandler payloadHandler,
                                                             DuplicateMessageDetection duplicateDetection,
                                                             UftpErrorProcessor errorProcessor) {
        return new ReceivedMessageProcessor(payloadHandler, duplicateDetection, errorProcessor);
    }

    @ConditionalOnMissingBean
    @Bean
    public UftpSerializer uftpSerializer(XsdValidator xsdValidator) {
        return new UftpSerializer(new XmlSerializer(), xsdValidator);
    }

    @ConditionalOnMissingBean
    @Bean
    public ParticipantResolutionService participantResolutionService(UftpParticipantService uftpParticipantService) {
        return new ParticipantResolutionService(uftpParticipantService);
    }

    @ConditionalOnMissingBean
    @Bean
    public ParticipantAuthorizationProvider participantOAuth2TokenProvider() {
        return new ParticipantAuthorizationProvider() {
            @Override
            public String getAuthorizationHeader(UftpParticipant participant) {
                throw new UnsupportedOperationException("It is required to provide an custom implementation of ParticipantAuthorizationProvider when authorization is used.");
            }
        };
    }

    @ConditionalOnMissingBean
    @Bean
    public UftpValidationService uftpValidationService(ParticipantSupport participantSupport,
                                                       CongestionPointSupport congestionPointSupport,
                                                       DuplicateMessageDetection duplicateMessageDetection,
                                                       UftpMessageSupport uftpMessageSupport,
                                                       UftpValidatorSupport uftpValidatorSupport,
                                                       ContractSupport contractSupport,
                                                       Set<UftpUserDefinedValidator<?>> uftpUserDefinedValidators) {
        var validators = new HashSet<>(Set.of(
                new AllowedSenderValidator(participantSupport),
                new CongestionPointValidator(congestionPointSupport),
                new DuplicateIdentifierValidator(duplicateMessageDetection),
                new ExpirationBeforeIspsListedEndValidator(),
                new ExpirationInTheFutureValidator(),
                new FlexOrderOfferIsNotRevokedValidator(uftpMessageSupport),
                new IspConflictsValidator(),
                new IspDurationValidator(uftpValidatorSupport),
                new IspPeriodBoundaryValidator(),
                new NotExpiredValidator(uftpMessageSupport),
                new PeriodFutureOrTodayValidator(),
                new PeriodIsInRangeValidator(),
                new PeriodReferenceValidator(uftpMessageSupport),
                new PeriodStartBeforeOrEqualsToEndValidator(),
                new RecipientValidator(participantSupport),
                new ReferencedBaselineReferenceValidator(uftpValidatorSupport),
                new ReferencedContractIdValidator(contractSupport),
                new ReferencedDPrognosisMessageIdValidator(uftpMessageSupport),
                new ReferencedFlexOfferMessageIdValidator(uftpMessageSupport),
                new ReferencedFlexOrderMessageIdValidator(uftpMessageSupport),
                new ReferencedFlexOrderOptionReferenceValidator(uftpMessageSupport),
                new ReferencedFlexRequestMessageIdValidator(uftpMessageSupport),
                new ReferencedFlexSettlementResponseOrderReferenceValidator(uftpMessageSupport),
                new ReferencedRequestMessageIdInResponseValidator(uftpMessageSupport),
                new SenderMatchesEnvelopeValidator(),
                new SenderRoleMatchesContentTypeValidator(),
                new TimeZoneSupportedValidator(uftpValidatorSupport),
                new FlexOptionRequestMatchValidator(uftpMessageSupport),
                new FlexOrderFlexibilityMatchValidator(uftpMessageSupport),
                new FlexOrderIspMatchValidator(uftpMessageSupport),
                new FlexOrderPriceMatchValidator(uftpMessageSupport),
                new IspPowerDiscrepancyValidator(),
                new IspRequestedDispositionValidator(),
                new MinActivationFactorValidator()
        ));

        validators.addAll(uftpUserDefinedValidators);

        return new UftpValidationService(validators);
    }

    @ConditionalOnMissingBean
    @Bean
    public DuplicateMessageDetection duplicateMessageDetection(UftpMessageSupport uftpMessageSupport) {
        return new DuplicateMessageDetection(uftpMessageSupport, new XmlSerializer());
    }

    @ConditionalOnMissingBean
    @Bean
    public UftpReceivedMessageService uftpReceivedMessageService(UftpValidationService uftpValidationService,
                                                                 UftpPayloadHandler uftpPayloadHandler) {
        var uftpReceivedMessageService = new UftpReceivedMessageService(uftpValidationService, uftpPayloadHandler);

        if (properties != null && properties.validation() != null) {
            uftpReceivedMessageService.setShouldPerformValidations(properties.validation().enabled());
        }

        return uftpReceivedMessageService;
    }

    @ConditionalOnMissingBean
    @Bean
    public XsdFactory xsdFactory() {
        return new XsdFactory(new XsdSchemaFactoryPool());
    }

    @ConditionalOnMissingBean
    @Bean
    public LazySodiumBase64Pool lazySodiumBase64Pool() {
        return new LazySodiumBase64Pool();
    }

    @ConditionalOnMissingBean
    @Bean
    public XsdValidator xsdValidator(XsdSchemaProvider xsdSchemaProvider) {
        return new XsdValidator(xsdSchemaProvider);
    }

    @ConditionalOnMissingBean
    @Bean
    public XsdSchemaProvider xsdSchemaProvider(XsdFactory xsdFactory) {
        return new XsdSchemaProvider(xsdFactory);
    }

    /**
     * Creates an {@link HttpClient} with an optional {@link SSLContext} with mutual TLS support (if configured).
     */
    private HttpClient httpClient() {
        var builder = HttpClient.newBuilder();

        if (properties.tls() != null) {
            log.info("Detected TLS configuration");
            builder.sslContext(SSLContextFactory.createSSLContext(properties.tls()));
        }

        return builder.build();
    }


}
