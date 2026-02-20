// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.spring.config;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

@ConfigurationProperties(prefix = "shapeshifter")
public record ShapeshifterProperties(
        ValidationProperties validation,
        TlsProperties tls,
        HttpProperties http
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

    }

    public record HttpProperties(
            Duration connectTimeout,
            Duration readTimeout
    ) { }
}
