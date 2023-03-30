// Copyright 2023 Contributors to the Shapeshifter project
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.shapeshifter.connector.application;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@Configuration
@EnableSpringConfigured
@Import({UftpControllerTestApp.class})
@ComponentScan(basePackages = {"org.lfenergy.shapeshifter"}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.CUSTOM, value = ExcludeTestMappingFilter.class)
})
@EnableConfigurationProperties
public class TestSpringConfigExcludingTestMappings {
}
