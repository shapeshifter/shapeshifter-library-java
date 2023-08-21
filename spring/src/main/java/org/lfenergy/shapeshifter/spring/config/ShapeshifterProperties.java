// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.config;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "shapeshifter")
public record ShapeshifterProperties(
        ValidationProperties validation,
        TlsProperties tls
) {
    public record ValidationProperties(
            boolean enabled
    ) {
    }

    @CommonsLog
    public record TlsProperties(
            String tlsVersion,
            Resource keyStore,
            String keyStorePassword,
            Resource trustStore,
            String trustStorePassword,
            Boolean useDefaultTrustStore
    ) {

        public static final String DEFAULT_TLS_VERSION = "TLSv1.3";

        public SSLContext createSSLContext() {
            KeyManager[] keyManagers = null;

            if (keyStore != null) {
                try {
                    var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                    keyManagerFactory.init(loadKeyStore(), keyStorePassword.toCharArray());

                    keyManagers = keyManagerFactory.getKeyManagers();
                } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
                    throw new IllegalArgumentException("Unable to initialize key manager factory: " + e.getMessage(), e);
                }
            }

            List<TrustManager> trustManagers = null;

            if (trustStore != null) {
                try {
                    trustManagers = new ArrayList<>();
                    if (useDefaultTrustStore == null || useDefaultTrustStore) {
                        trustManagers.addAll(Arrays.asList(getDefaultTrustManagers()));
                    }

                    var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    trustManagerFactory.init(loadTrustStore());

                    trustManagers.addAll(Arrays.asList(trustManagerFactory.getTrustManagers()));
                } catch (NoSuchAlgorithmException | KeyStoreException e) {
                    throw new IllegalArgumentException("Unable to initialize trust manager factory: " + e.getMessage(), e);
                }
            }

            try {
                var protocol = tlsVersion != null ? tlsVersion : DEFAULT_TLS_VERSION;

                log.info("Initializing SSL context with protocol: " + protocol);
                var sslContext = SSLContext.getInstance(protocol);
                sslContext.init(keyManagers, trustManagers != null ? trustManagers.toArray(TrustManager[]::new) : null, null);

                return sslContext;
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new IllegalArgumentException("Unable to initialize SSL context: " + e.getMessage(), e);
            }
        }

        private TrustManager[] getDefaultTrustManagers() throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null); // null means use default trust store from JRE

           return tmf.getTrustManagers();
        }

        private KeyStore loadKeyStore() {
            try {
                log.info("Loading key store: " + keyStore.getDescription());
                return KeyStore.getInstance(keyStore.getFile(), keyStorePassword.toCharArray());
            } catch (IOException | GeneralSecurityException e) {
                throw new IllegalArgumentException("Unable to load key store: " + keyStore, e);
            }
        }

        private KeyStore loadTrustStore() {
            try {
                log.info("Loading trust store: " + trustStore.getDescription());
                return KeyStore.getInstance(trustStore.getFile(), trustStorePassword.toCharArray());
            } catch (IOException | GeneralSecurityException e) {
                throw new IllegalArgumentException("Unable to load trust store: " + trustStore, e);
            }
        }

    }
}
