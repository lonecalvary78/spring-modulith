/*
 * Copyright 2020-2025 the original author or authors.
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
package org.springframework.modulith.core;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Violations}.
 *
 * @author Oliver Drotbohm
 */
class ViolationsUnitTests {

	@Test
	void combinesExceptionMessages() {

		Violations violations = Violations.NONE //
				.and("First") //
				.and("Second");

		assertThat(violations.getMessage()) //
				.isEqualTo("- First\n- Second");
	}

	@Test
	void deduplicatesViolationsWithTheSameMessage() {

		var violations = Violations.NONE
				.and("First")
				.and("First")
				.and("Second")
				.and(Violations.NONE.and("First")
						.and("Second")
						.and("Third"));

		assertThat(violations.getMessages())
				.containsExactlyInAnyOrder("First", "Second", "Third");
	}

	@Test // GH-995
	void allowsFilteringViolations() {

		var violations = Violations.NONE.and("First").and("Second")
				.filter(it -> it.hasMessageContaining("Sec"));

		assertThat(violations.getMessages())
				.containsExactly("Second");
	}
}
