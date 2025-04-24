// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.shapeshifter.api.*;
import org.lfenergy.shapeshifter.api.model.UftpParticipantInformation;
import org.lfenergy.shapeshifter.core.model.IncomingUftpMessage;
import org.lfenergy.shapeshifter.core.model.OutgoingUftpMessage;
import org.lfenergy.shapeshifter.core.model.UftpParticipant;
import org.lfenergy.shapeshifter.core.service.UftpErrorProcessor;
import org.lfenergy.shapeshifter.core.service.UftpParticipantService;
import org.lfenergy.shapeshifter.core.service.crypto.UftpCryptoService;
import org.lfenergy.shapeshifter.core.service.receiving.UftpReceivedMessageService;
import org.lfenergy.shapeshifter.core.service.serialization.UftpSerializer;
import org.lfenergy.shapeshifter.core.service.validation.*;
import org.lfenergy.shapeshifter.spring.ShapeshifterSpringIntegrationTest.TestConfig;
import org.lfenergy.shapeshifter.spring.config.EnableShapeshifter;
import org.lfenergy.shapeshifter.spring.service.handler.UftpIncomingHandler;
import org.lfenergy.shapeshifter.spring.service.handler.UftpOutgoingHandler;
import org.lfenergy.shapeshifter.spring.service.receiving.UftpInternalController;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.shapeshifter.api.USEFRoleType.AGR;
import static org.lfenergy.shapeshifter.api.USEFRoleType.DSO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UftpInternalController.class)
@ContextConfiguration(classes = TestConfig.class)
class ShapeshifterSpringIntegrationTest {

  private static final String APPLICATION_ENDPOINT_PATH = "/shapeshifter/api/v3/message";
  private static final String MESSAGE_ID = UUID.randomUUID().toString();

  private static final String DSO_DOMAIN = "dso.tld";
  private static final String AGR_DOMAIN = "agr.tld";
  private static final UftpParticipant DSO_PARTICIPANT = new UftpParticipant(DSO_DOMAIN, USEFRoleType.DSO);
  private static final UftpParticipant AGR_PARTICIPANT = new UftpParticipant(AGR_DOMAIN, USEFRoleType.AGR);

  private static final String TEST_PRIVATE_KEY = "4niUT8PXmRC5syAubh4lfvYM0Q83q/Clo/cb+Ey7frLQS41+XClbwutwyuw/kmtsHgbY34XrTOWtron8Nt7MVA==";
  private static final String TEST_PUBLIC_KEY = "0EuNflwpW8LrcMrsP5JrbB4G2N+F60zlra6J/DbezFQ=";

  private static final String CONVERSATION_ID = UUID.randomUUID().toString();
  private static final String CONTRACT_ID = UUID.randomUUID().toString();
  private static final String CONGESTION_POINT = "ean.123123123123123123";
  private static final OffsetDateTime NOW = OffsetDateTime.now();
  private static final String TIME_ZONE = "Europe/Amsterdam";
  private static final String VERSION = "3.1.0";

  @Configuration
  @EnableShapeshifter
  static class TestConfig {
  }

  @MockBean
  private UftpParticipantService uftpParticipantService;
  @MockBean
  private UftpValidatorSupport uftpValidatorSupport;
  @MockBean
  private ParticipantSupport participantSupport;
  @MockBean
  private UftpMessageSupport uftpMessageSupport;
  @MockBean
  private ContractSupport contractSupport;
  @MockBean
  private CongestionPointSupport congestionPointSupport;
  @MockBean
  private UftpIncomingHandler<PayloadMessageType> uftpIncomingHandler;
  @MockBean
  private UftpOutgoingHandler<PayloadMessageType> uftpOutgoingHandler;
  @MockBean
  private UftpErrorProcessor uftpErrorProcessor;

  @Autowired
  private UftpSerializer serializer;
  @Autowired
  private UftpCryptoService cryptoService;
  @Autowired
  private UftpReceivedMessageService uftpReceivedMessageService;

  @Autowired
  private MockMvc mockMvc;

  @Captor
  private ArgumentCaptor<IncomingUftpMessage<PayloadMessageType>> incomingUftpMessageCaptor;
  @Captor
  private ArgumentCaptor<OutgoingUftpMessage<PayloadMessageType>> outgoingUftpMessageCaptor;

  @BeforeEach
  void setUp() {
    given(uftpIncomingHandler.isSupported(any())).willReturn(true);
    given(uftpOutgoingHandler.isSupported(any())).willReturn(true);
  }

  @Test
  void postUftpMessage_flexRequest_happyFlow() throws Exception {
    mockUftpParticipants();
    mockValidatorSupport();

    var flexRequest = createTestFlexRequest();

    mockMvc.perform(post(APPLICATION_ENDPOINT_PATH)
                        .contentType(MediaType.TEXT_XML)
                        .content(createSignedMessageXml(flexRequest)))
           .andExpect(status().isOk());

    verify(uftpIncomingHandler).handle(incomingUftpMessageCaptor.capture());

    var incomingUftpMessage = incomingUftpMessageCaptor.getValue();
    assertThat(incomingUftpMessage.sender()).isEqualTo(DSO_PARTICIPANT);

    var request = (FlexRequest) incomingUftpMessage.payloadMessage();
    assertThat(request).usingRecursiveComparison().isEqualTo(flexRequest);

    uftpReceivedMessageService.process(incomingUftpMessage);

    verify(uftpOutgoingHandler).handle(outgoingUftpMessageCaptor.capture());

    var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
    assertThat(outgoingUftpMessage.sender()).isEqualTo(AGR_PARTICIPANT);
    var response = (FlexRequestResponse) outgoingUftpMessage.payloadMessage();
    assertThat(response.getFlexRequestMessageID()).isEqualTo(flexRequest.getMessageID());
    assertThat(response.getResult()).isEqualTo(AcceptedRejectedType.ACCEPTED);
    assertThat(response.getRejectionReason()).isBlank();
    assertThat(response.getSenderDomain()).isEqualTo(AGR_DOMAIN);
    assertThat(response.getRecipientDomain()).isEqualTo(DSO_DOMAIN);
  }

  @Test
  void postUftpMessage_flexRequest_duplicate() throws Exception {
    mockUftpParticipants();
    mockValidatorSupport();

    var flexRequest = createTestFlexRequest();

    given(uftpMessageSupport.findDuplicateMessage(flexRequest.getMessageID(), flexRequest.getSenderDomain(), flexRequest.getRecipientDomain())).willReturn(Optional.of(flexRequest));

    mockMvc.perform(post(APPLICATION_ENDPOINT_PATH)
                        .contentType(MediaType.TEXT_XML)
                        .content(createSignedMessageXml(flexRequest)))
           .andExpect(status().isBadRequest());

    verifyNoInteractions(uftpIncomingHandler);
  }

  @Test
  void postUftpMessage_flexRequest_validationError() throws Exception {
    mockUftpParticipants();
    mockValidatorSupport();

    var flexRequest = createTestFlexRequest();
    flexRequest.setISPDuration(Duration.ofMinutes(30));

    mockMvc.perform(post(APPLICATION_ENDPOINT_PATH)
                        .contentType(MediaType.TEXT_XML)
                        .content(createSignedMessageXml(flexRequest)))
           .andExpect(status().isOk());

    verify(uftpIncomingHandler).handle(incomingUftpMessageCaptor.capture());

    var incomingUftpMessage = incomingUftpMessageCaptor.getValue();
    assertThat(incomingUftpMessage.sender()).isEqualTo(DSO_PARTICIPANT);
    var request = (FlexRequest) incomingUftpMessage.payloadMessage();
    assertThat(request).usingRecursiveComparison().isEqualTo(flexRequest);

    uftpReceivedMessageService.process(incomingUftpMessage);

    verify(uftpOutgoingHandler).handle(outgoingUftpMessageCaptor.capture());

    var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
    assertThat(outgoingUftpMessage.sender()).isEqualTo(AGR_PARTICIPANT);
    var response = (FlexRequestResponse) outgoingUftpMessage.payloadMessage();
    assertThat(response.getFlexRequestMessageID()).isEqualTo(flexRequest.getMessageID());
    assertThat(response.getResult()).isEqualTo(AcceptedRejectedType.REJECTED);
    assertThat(response.getRejectionReason()).isEqualTo("ISP duration rejected");
    assertThat(response.getSenderDomain()).isEqualTo(AGR_DOMAIN);
    assertThat(response.getRecipientDomain()).isEqualTo(DSO_DOMAIN);
  }

  @Test
  void postUftpMessage_testMessage_happyFlow() throws Exception {
    mockUftpParticipants();
    mockValidatorSupport();

    var testMessage = createTestMessage();

    mockMvc.perform(post(APPLICATION_ENDPOINT_PATH)
                        .contentType(MediaType.TEXT_XML)
                        .content(createSignedMessageXml(testMessage)))
           .andExpect(status().isOk());

    verify(uftpIncomingHandler).handle(incomingUftpMessageCaptor.capture());

    var incomingUftpMessage = incomingUftpMessageCaptor.getValue();
    assertThat(incomingUftpMessage.sender()).isEqualTo(DSO_PARTICIPANT);
    var request = (TestMessage) incomingUftpMessage.payloadMessage();
    assertThat(request).usingRecursiveComparison().isEqualTo(testMessage);

    uftpReceivedMessageService.process(incomingUftpMessage);

    verify(uftpOutgoingHandler).handle(outgoingUftpMessageCaptor.capture());

    var outgoingUftpMessage = outgoingUftpMessageCaptor.getValue();
    assertThat(outgoingUftpMessage.sender()).isEqualTo(AGR_PARTICIPANT);
    var response = (TestMessageResponse) outgoingUftpMessage.payloadMessage();
    assertThat(response.getSenderDomain()).isEqualTo(AGR_DOMAIN);
    assertThat(response.getRecipientDomain()).isEqualTo(DSO_DOMAIN);
    assertThat(response.getConversationID()).isEqualTo(testMessage.getConversationID());
  }

  private void mockUftpParticipants() {
    given(uftpParticipantService.getParticipantInformation(AGR, AGR_DOMAIN)).willReturn(Optional.of(new UftpParticipantInformation(AGR_DOMAIN, TEST_PUBLIC_KEY, "aEndpoint", false)));
    given(uftpParticipantService.getParticipantInformation(DSO, DSO_DOMAIN)).willReturn(Optional.of(new UftpParticipantInformation(DSO_DOMAIN, TEST_PUBLIC_KEY, "aEndpoint", false)));
  }

  private void mockValidatorSupport() {
    given(participantSupport.isHandledRecipient(AGR_PARTICIPANT)).willReturn(true);
    given(participantSupport.isHandledRecipient(new UftpParticipant(DSO_DOMAIN, DSO))).willReturn(true);
    given(participantSupport.isAllowedSender(any(UftpParticipant.class))).willReturn(true);
    given(contractSupport.isSupportedContractID(any(String.class))).willReturn(true);
    given(congestionPointSupport.areKnownCongestionPoints(any(Collection.class))).willReturn(true);
    given(uftpValidatorSupport.isSupportedIspDuration(Duration.ofMinutes(15))).willReturn(true);
    given(uftpValidatorSupport.isSupportedTimeZone(TimeZone.getTimeZone(TIME_ZONE))).willReturn(true);
  }

  private String createSignedMessageXml(PayloadMessageType payloadMessage) {
    return serializer.toXml(cryptoService.signMessage(serializer.toXml(payloadMessage), DSO_PARTICIPANT, TEST_PRIVATE_KEY));
  }

  private static FlexRequest createTestFlexRequest() {
    var flexRequest = new FlexRequest();
    flexRequest.setMessageID(MESSAGE_ID);
    flexRequest.setConversationID(CONVERSATION_ID);
    flexRequest.setContractID(CONTRACT_ID);
    flexRequest.setSenderDomain(DSO_DOMAIN);
    flexRequest.setRecipientDomain(AGR_DOMAIN);
    flexRequest.setPeriod(NOW.plusDays(7).toLocalDate());
    flexRequest.setExpirationDateTime(NOW.plusDays(1));
    flexRequest.setCongestionPoint(CONGESTION_POINT);
    flexRequest.setISPDuration(Duration.ofMinutes(15));
    flexRequest.setTimeStamp(NOW);
    flexRequest.setTimeZone(TIME_ZONE);
    flexRequest.setRevision(1);
    flexRequest.setVersion(VERSION);

    var isp = new FlexRequestISPType();
    isp.setDuration(1L);
    isp.setDisposition(AvailableRequestedType.REQUESTED);
    isp.setMaxPower(500000L);
    isp.setMinPower(0L);
    isp.setStart(5L);

    flexRequest.getISPS().add(isp);

    return flexRequest;
  }

  private static TestMessage createTestMessage() {
    var testMessage = new TestMessage();
    testMessage.setMessageID(MESSAGE_ID);
    testMessage.setConversationID(CONVERSATION_ID);
    testMessage.setSenderDomain(DSO_DOMAIN);
    testMessage.setRecipientDomain(AGR_DOMAIN);
    testMessage.setTimeStamp(NOW);
    testMessage.setVersion(VERSION);
    return testMessage;
  }

}