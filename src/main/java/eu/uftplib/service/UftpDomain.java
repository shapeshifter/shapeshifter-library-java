package eu.uftplib.service;

public class UftpDomain {
    private String Endpoint;
    private String PublicKey;

    public UftpDomain(String endpoint, String publicKey) {
        this.Endpoint = endpoint;
        this.PublicKey = publicKey;
    }

    public String getEndpoint() {
        return this.Endpoint;
    }

    public String getPublicKey() {
        return this.PublicKey;
    }

}