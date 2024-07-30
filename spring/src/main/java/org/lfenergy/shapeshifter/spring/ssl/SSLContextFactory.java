package org.lfenergy.shapeshifter.spring.ssl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.lfenergy.shapeshifter.spring.config.ShapeshifterProperties;
import org.springframework.core.io.Resource;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@CommonsLog
public class SSLContextFactory {

    public static SSLContext createSSLContext(ShapeshifterProperties.TlsProperties properties) {
        KeyManager[] keyManagers = null;

        if (properties.keyStore() != null) {
            try {
                var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(loadKeyStore(properties.keyStore(), properties.keyStorePassword()), properties.keyStorePassword().toCharArray());

                keyManagers = keyManagerFactory.getKeyManagers();
            } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
                throw new IllegalArgumentException("Unable to initialize key manager: " + e.getMessage(), e);
            }
        }

        TrustManager[] trustManagers = null;

        if (properties.trustStore() != null) {
            try {
                var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(loadTrustStore(properties.trustStore(), properties.trustStorePassword()));

                var x509TrustManager = getX509TrustManager(trustManagerFactory);

                if (properties.useDefaultTrustStore() == null || properties.useDefaultTrustStore()) {
                    // Combine the default trust manager with the one from the trust store
                    x509TrustManager = new CombinedX509TrustManager(x509TrustManager, getDefaultX509TrustManager());
                }

                trustManagers = new TrustManager[]{x509TrustManager};
            } catch (NoSuchAlgorithmException | KeyStoreException e) {
                throw new IllegalArgumentException("Unable to initialize trust manager: " + e.getMessage(), e);
            }
        }

        try {
            var protocol = properties.tlsVersion() != null ? properties.tlsVersion() : ShapeshifterProperties.TlsProperties.DEFAULT_TLS_VERSION;

            log.info("Initializing SSL context with protocol: " + protocol);
            var sslContext = SSLContext.getInstance(protocol);
            sslContext.init(keyManagers, trustManagers, null);

            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalArgumentException("Unable to initialize SSL context: " + e.getMessage(), e);
        }
    }

    private static X509TrustManager getDefaultX509TrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null); // null means use default trust store from JRE

        return getX509TrustManager(trustManagerFactory);
    }

    private static X509TrustManager getX509TrustManager(TrustManagerFactory trustManagerFactory) {
        for (var trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager x509TrustManager) {
                return x509TrustManager;
            }
        }
        throw new IllegalStateException("No X509TrustManager found");
    }

    private static KeyStore loadKeyStore(Resource keyStore, String keyStorePassword) {
        try {
            log.info("Loading key store: " + keyStore.getDescription());
            return KeyStore.getInstance(keyStore.getFile(), keyStorePassword.toCharArray());
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalArgumentException("Unable to load key store: " + keyStore, e);
        }
    }

    private static KeyStore loadTrustStore(Resource trustStore, String trustStorePassword) {
        try {
            log.info("Loading trust store: " + trustStore.getDescription());
            return KeyStore.getInstance(trustStore.getFile(), trustStorePassword.toCharArray());
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalArgumentException("Unable to load trust store: " + trustStore, e);
        }
    }

}
