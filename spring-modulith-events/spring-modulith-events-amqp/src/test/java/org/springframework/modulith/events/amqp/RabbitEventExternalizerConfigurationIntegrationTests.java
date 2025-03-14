/*
 * Copyright 2023-2025 the original author or authors.
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
package org.springframework.modulith.events.amqp;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitMessageOperations;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.modulith.events.EventExternalizationConfiguration;
import org.springframework.modulith.events.support.DelegatingEventExternalizer;

/**
 * Integration tests for {@link RabbitEventExternalizerConfiguration}.
 *
 * @author Oliver Drotbohm
 * @since 1.1
 */
class RabbitEventExternalizerConfigurationIntegrationTests {

	@Test // GH-342
	void registersExternalizerByDefault() {

		basicSetup()
				.run(ctxt -> {
					assertThat(ctxt).hasSingleBean(DelegatingEventExternalizer.class);
				});
	}

	@Test // GH-342
	void disablesExternalizationIfConfigured() {

		basicSetup()
				.withPropertyValues("spring.modulith.events.externalization.enabled=false")
				.run(ctxt -> {
					assertThat(ctxt).doesNotHaveBean(DelegatingEventExternalizer.class);
				});
	}

	private ApplicationContextRunner basicSetup() {

		return new ApplicationContextRunner()
				.withConfiguration(
						AutoConfigurations.of(RabbitEventExternalizerConfiguration.class))
				.withBean(EventExternalizationConfiguration.class, () -> EventExternalizationConfiguration.disabled())
				.withBean(RabbitMessageOperations.class, () -> mock(RabbitMessageOperations.class));
	}
}
