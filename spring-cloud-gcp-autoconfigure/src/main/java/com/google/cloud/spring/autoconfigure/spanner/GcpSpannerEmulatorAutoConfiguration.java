/*
 * Copyright 2017-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.autoconfigure.spanner;

import com.google.cloud.NoCredentials;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spring.core.GcpProjectIdProvider;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * Provides auto-configuration to use the Spanner emulator if enabled.
 *
 * @author Eddú Meléndez
 * @author Eddy Kioi
 * @since 1.2.3
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(GcpSpannerAutoConfiguration.class)
@EnableConfigurationProperties(GcpSpannerProperties.class)
@ConditionalOnProperty(prefix = "spring.cloud.gcp.spanner.emulator", name = "enabled", havingValue = "true")
public class GcpSpannerEmulatorAutoConfiguration {

	private final GcpSpannerProperties properties;

	private final String projectId;

	public GcpSpannerEmulatorAutoConfiguration(GcpSpannerProperties properties,
			GcpProjectIdProvider projectIdProvider) {

		this.projectId = (properties.getProjectId() != null)
				? properties.getProjectId()
				: projectIdProvider.getProjectId();

		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	public SpannerOptions spannerOptions() {
		Assert.notNull(this.properties.getEmulatorHost(), "`spring.cloud.gcp.spanner.emulator-host` must be set.");
		return SpannerOptions.newBuilder()
				.setProjectId(this.projectId)
				.setCredentials(NoCredentials.getInstance())
				.setEmulatorHost(this.properties.getEmulatorHost()).build();
	}
}
