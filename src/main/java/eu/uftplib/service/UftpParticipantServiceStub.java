package eu.uftplib.service;

public class UftpParticipantServiceStub implements UftpParticipantService {
    public UftpDomain getUftpDomainDetails(String domain) {
        switch (domain) {
            case "a" : return new UftpDomain("http://localhost:8080", "ABCDEFG");
            case "b" : return new UftpDomain("http://localhost:8081", "GFEDCAB");
        }
        return null;
    }
}
