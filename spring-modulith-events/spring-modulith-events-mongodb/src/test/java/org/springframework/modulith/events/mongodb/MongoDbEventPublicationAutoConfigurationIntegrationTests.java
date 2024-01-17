/*
 * Copyright 2022-2024 the original author or authors.
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
package org.springframework.modulith.events.mongodb;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.modulith.events.core.EventPublicationRegistry;
import org.springframework.modulith.testapp.TestApplication;

/**
 * @author Dmitry Belyaev
 * @author Björn Kieling
 * @author Oliver Drotbohm
 */
@SpringBootTest(classes = TestApplication.class)
class MongoDbEventPublicationAutoConfigurationIntegrationTests {

	@Autowired ApplicationContext context;

	@Test // GH-4, GH-175
	void bootstrapsApplicationComponents() {

		assertThat(context.getBean(EventPublicationRegistry.class)).isNotNull();
		assertThat(context.getBean(MongoDbEventPublicationRepository.class)).isNotNull();
		assertThat(context.getBean(MongoTransactionManager.class)).isNotNull();
	}
}
