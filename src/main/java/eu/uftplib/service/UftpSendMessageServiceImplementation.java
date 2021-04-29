// SPDX-FileCopyrightText: 2020-2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package eu.uftplib.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class UftpSendMessageServiceImplementation implements UftpSendMessageService {
    private UftpParticipantService uftpParticipantService;
    private UftpSigningService uftpSigningService;

    public UftpSendMessageServiceImplementation(UftpParticipantService uftpParticipantService, UftpSigningService uftpSigningService) {
        this.uftpParticipantService = uftpParticipantService;
        this.uftpSigningService = uftpSigningService;
    }

    public boolean sendMessage(String xml, String privateKey, DomainPair domainPair) {

        var uftpDomain = uftpParticipantService.getUftpDomainDetails(domainPair.getRecipientDomain());
        var signedXml = uftpSigningService.sealMessage(xml, privateKey, domainPair);

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uftpDomain.getEndpoint())).POST(BodyPublishers.ofString(signedXml))
            .build();

        HttpResponse<?> response;
        try {
            response = client.send(request, BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            return false;
        }
        return (response.statusCode() == 200);
    }
}
