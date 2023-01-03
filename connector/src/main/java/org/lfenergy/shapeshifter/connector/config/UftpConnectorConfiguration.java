package org.lfenergy.shapeshifter.connector.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UftpConnectorConfig.class)
public class UftpConnectorConfiguration {}
