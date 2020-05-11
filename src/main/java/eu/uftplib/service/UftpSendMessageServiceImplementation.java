package eu.uftplib.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class UftpSendMessageServiceImplementation implements UftpSendMessageService {
    public boolean sendMessage(final String xml, final String endpoint) {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endpoint)).POST(BodyPublishers.ofString(xml))
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
