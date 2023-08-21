package org.lfenergy.shapeshifter.spring.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.*;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * {@link X509TrustManager} that will trust a certificate if one of the underlying {@link X509TrustManager}s trusts it.
 */
class CombinedX509TrustManager implements X509TrustManager {

    private final X509TrustManager trustManager;
    private final X509TrustManager defaultTrustManager;

    CombinedX509TrustManager(X509TrustManager trustManager, X509TrustManager defaultTrustManager) {
        this.trustManager = trustManager;
        this.defaultTrustManager = defaultTrustManager;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            trustManager.checkClientTrusted(chain, authType);
        } catch (CertificateExpiredException| CertificateNotYetValidException | CertificateRevokedException e) {
            throw e;
        } catch (CertificateException e) {
            defaultTrustManager.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            trustManager.checkClientTrusted(chain, authType);
        } catch (CertificateExpiredException| CertificateNotYetValidException | CertificateRevokedException e) {
            throw e;
        } catch (CertificateException e) {
            defaultTrustManager.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return Stream.concat(
                        Arrays.stream(trustManager.getAcceptedIssuers()),
                        Arrays.stream(defaultTrustManager.getAcceptedIssuers()))
                .toArray(X509Certificate[]::new);
    }
}
