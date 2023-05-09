// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.config;

import java.util.HashSet;
import java.util.Set;
import org.lfenergy.shapeshifter.core.common.xml.XmlSerializer;
import org.lfenergy.shapeshifter.core.common.xsd.XsdFactory;
import org.lfenergy.shapeshifter.core.common.xsd.XsdSchemaFactoryPool;
import org.lfenergy.shapeshifter.core.common.xsd.XsdSchemaProvider;
import org.lfenergy.shapeshifter.core.common.xsd.XsdValidator;
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
import org.lfenergy.shapeshifter.core.service.validation.CongestionPointSupport;
import org.lfenergy.shapeshifter.core.service.validation.ContractSupport;
import org.lfenergy.shapeshifter.core.service.validation.ParticipantSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpMessageSupport;
import org.lfenergy.shapeshifter.core.service.validation.UftpUserDefinedValidator;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidationService;
import org.lfenergy.shapeshifter.core.service.validation.UftpValidatorSupport;
import org.lfenergy.shapeshifter.core.service.validation.base.AllowedSenderValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.CongestionPointValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.DuplicateIdentifierValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ExpirationBeforeIspsListedEndValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ExpirationInTheFutureValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.FlexOrderOfferIsNotRevokedValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.IspConflictsValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.IspDurationValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.IspPeriodBoundaryValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.NotExpiredValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.PeriodFutureOrTodayValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.PeriodIsInRangeValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.PeriodReferenceValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.PeriodStartBeforeOrEqualsToEndValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.RecipientValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedBaselineReferenceValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedContractIdValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedDPrognosisMessageIdValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedFlexOfferMessageIdValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedFlexOrderMessageIdValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedFlexOrderOptionReferenceValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedFlexRequestMessageIdValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedFlexSettlementResponseOrderReferenceValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.ReferencedRequestMessageIdInResponseValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.SenderMatchesEnvelopeValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.SenderRoleMatchesContentTypeValidator;
import org.lfenergy.shapeshifter.core.service.validation.base.TimeZoneSupportedValidator;
import org.lfenergy.shapeshifter.core.service.validation.message.FlexOptionRequestMatchValidator;
import org.lfenergy.shapeshifter.core.service.validation.message.FlexOrderFlexibilityMatchValidator;
import org.lfenergy.shapeshifter.core.service.validation.message.FlexOrderIspMatchValidator;
import org.lfenergy.shapeshifter.core.service.validation.message.FlexOrderPriceMatchValidator;
import org.lfenergy.shapeshifter.core.service.validation.message.IspPowerDiscrepancyValidator;
import org.lfenergy.shapeshifter.core.service.validation.message.IspRequestedDispositionValidator;
import org.lfenergy.shapeshifter.core.service.validation.message.MinActivationFactorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(value = "org.lfenergy.shapeshifter")
@EnableConfigurationProperties(ShapeshifterProperties.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({WebMvcConfigurer.class})
public class ShapeshifterConfiguration {

  @Bean
  @Autowired
  public UftpSendMessageService uftpSendMessageService(UftpSerializer serializer,
                                                       UftpCryptoService cryptoService,
                                                       ParticipantResolutionService participantService,
                                                       UftpValidationService uftpValidationService) {
    return new UftpSendMessageService(serializer, cryptoService, participantService, uftpValidationService);
  }

  @Bean
  @Autowired
  public UftpCryptoService uftpCryptoService(ParticipantResolutionService participantService,
                                             LazySodiumFactory factory,
                                             LazySodiumBase64Pool lazySodiumInstancePool) {
    return new UftpCryptoService(participantService, factory, lazySodiumInstancePool);
  }

  @Bean
  public LazySodiumFactory lazySodiumFactory() {
    return new LazySodiumFactory();
  }

  @Bean
  @Autowired
  public ReceivedMessageProcessor receivedMessageProcessor(UftpPayloadHandler payloadHandler,
                                                           DuplicateMessageDetection duplicateDetection,
                                                           UftpErrorProcessor errorProcessor) {
    return new ReceivedMessageProcessor(payloadHandler, duplicateDetection, errorProcessor);
  }

  @Bean
  @Autowired
  public UftpSerializer uftpSerializer(XsdValidator xsdValidator) {
    return new UftpSerializer(new XmlSerializer(), xsdValidator);
  }

  @Bean
  @Autowired
  public ParticipantResolutionService participantResolutionService(UftpParticipantService uftpParticipantService) {
    return new ParticipantResolutionService(uftpParticipantService);
  }

  @Bean
  @Autowired
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

  @Bean
  @Autowired
  public DuplicateMessageDetection duplicateMessageDetection(UftpMessageSupport uftpMessageSupport) {
    return new DuplicateMessageDetection(uftpMessageSupport, new XmlSerializer());
  }

  @Bean
  @Autowired
  public UftpReceivedMessageService uftpReceivedMessageService(UftpValidationService uftpValidationService,
                                                               UftpPayloadHandler uftpPayloadHandler,
                                                               ShapeshifterProperties properties) {
    var uftpReceivedMessageService = new UftpReceivedMessageService(uftpValidationService, uftpPayloadHandler);

    if (properties != null && properties.validation() != null) {
      uftpReceivedMessageService.setShouldPerformValidations(properties.validation().enabled());
    }

    return uftpReceivedMessageService;
  }

  @Bean
  public XsdFactory xsdFactory() {
    return new XsdFactory(new XsdSchemaFactoryPool());
  }

  @Bean
  public LazySodiumBase64Pool lazySodiumBase64Pool() {
    return new LazySodiumBase64Pool();
  }

  @Bean
  @Autowired
  public XsdValidator xsdValidator(XsdSchemaProvider xsdSchemaProvider) {
    return new XsdValidator(xsdSchemaProvider);
  }

  @Bean
  @Autowired
  public XsdSchemaProvider xsdSchemaProvider(XsdFactory xsdFactory) {
    return new XsdSchemaProvider(xsdFactory);
  }

}
