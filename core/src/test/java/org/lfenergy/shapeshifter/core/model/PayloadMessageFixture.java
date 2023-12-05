// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.core.model;

import org.lfenergy.shapeshifter.api.AvailableRequestedType;
import org.lfenergy.shapeshifter.api.FlexRequest;
import org.lfenergy.shapeshifter.api.FlexRequestISPType;
import org.lfenergy.shapeshifter.api.FlexRequestResponse;
import org.lfenergy.shapeshifter.api.TestMessage;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PayloadMessageFixture {

    private static final String CONVERSATION_ID = UUID.randomUUID().toString();
    private static final String CONTRACT_ID = UUID.randomUUID().toString();
    private static final String CONGESTION_POINT = "ean.123123123123123123";
    private static final OffsetDateTime NOW = OffsetDateTime.now();
    private static final String TIME_ZONE = "Europe/Amsterdam";
    private static final String VERSION = "3.0.0";

    public static final Duration DURATION_15_MINUTES = Duration.ofMinutes(15);

    public static FlexRequest createTestFlexRequest(String messageId, String senderDomain, String recipientDomain) {
        var flexRequest = new FlexRequest();
        flexRequest.setMessageID(messageId);
        flexRequest.setConversationID(CONVERSATION_ID);
        flexRequest.setContractID(CONTRACT_ID);
        flexRequest.setSenderDomain(senderDomain);
        flexRequest.setRecipientDomain(recipientDomain);
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

    public static FlexRequestResponse createTestFlexRequestResponse(String messageId, String senderDomain, String recipientDomain) {
        var flexRequestResponse = new FlexRequestResponse();
        flexRequestResponse.setMessageID(messageId);
        flexRequestResponse.setConversationID(CONVERSATION_ID);
        flexRequestResponse.setSenderDomain(senderDomain);
        flexRequestResponse.setRecipientDomain(recipientDomain);
        flexRequestResponse.setTimeStamp(NOW);
        return flexRequestResponse;
    }

    public static TestMessage createTestMessage(String messageId, String senderDomain, String recipientDomain) {
        var testMessage = new TestMessage();
        testMessage.setMessageID(messageId);
        testMessage.setConversationID(CONVERSATION_ID);
        testMessage.setSenderDomain(senderDomain);
        testMessage.setRecipientDomain(recipientDomain);
        testMessage.setTimeStamp(NOW);
        testMessage.setVersion(VERSION);
        return testMessage;
    }

}
