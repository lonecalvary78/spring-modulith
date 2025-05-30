/*
 * Copyright 2022-2025 the original author or authors.
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
package org.springframework.modulith.actuator.autoconfigure;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.modulith.actuator.ApplicationModulesEndpoint;
import org.springframework.modulith.core.util.ApplicationModulesExporter;
import org.springframework.modulith.runtime.ApplicationModulesRuntime;
import org.springframework.util.function.ThrowingSupplier;

/**
 * Auto-configuration for the {@link ApplicationModulesEndpoint}.
 *
 * @author Oliver Drotbohm
 */
@AutoConfiguration
class ApplicationModulesEndpointConfiguration {

	private static final String FILE_LOCATION = ApplicationModulesExporter.DEFAULT_LOCATION;

	private static final Resource PRECOMPUTED = new ClassPathResource(FILE_LOCATION);
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationModulesEndpointConfiguration.class);

	@Bean
	@ConditionalOnMissingBean
	ApplicationModulesEndpoint applicationModulesEndpoint(ApplicationModulesRuntime runtime) {

		if (PRECOMPUTED.exists()) {

			ThrowingSupplier<String> fileContent = () -> PRECOMPUTED.getContentAsString(StandardCharsets.UTF_8);

			LOGGER.debug("Using application modules description from {}", FILE_LOCATION);
			return ApplicationModulesEndpoint.precomputed(fileContent);

		} else {
			return ApplicationModulesEndpoint.ofApplicationModules(runtime);
		}
	}
}
