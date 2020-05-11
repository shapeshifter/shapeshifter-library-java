package eu.uftplib.service;

public class UftpParticipantServiceStub implements UftpParticipantService {
    public UftpDomain getUftpDomainDetails(String domain) {
        switch (domain) {
            case "DomainA" : return new UftpDomain("http://localhost:8080", "ABCDEFG");
            case "DomainB" : return new UftpDomain("http://localhost:8081", "GFEDCAB");
        }
        return null;
    }
}
