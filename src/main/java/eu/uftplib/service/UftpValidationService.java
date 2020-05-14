package eu.uftplib.service;

public interface UftpValidationService {
    DomainPair validateXml(String xml, MessageDirection messageDirection);
}
