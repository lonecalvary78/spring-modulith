/*
 * Copyright 2024-2025 the original author or authors.
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
package org.springframework.modulith.core.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Findbugs and IDEA handle any annotation with name "CheckReturnValue" in return value check.
 *
 * @see <a href=
 *      "https://github.com/findbugsproject/findbugs/blob/264ae7baf890d2b347d91805c90057062b5dcb1e/findbugs/src/java/edu/umd/cs/findbugs/detect/BuildCheckReturnAnnotationDatabase.java#L120">Findbugs
 *      source code</a>
 * @since 1.3
 * @deprecated since 1.4, use Spring Framework's {@link org.springframework.lang.CheckReturnValue} instead.
 */
@Target({
		ElementType.CONSTRUCTOR,
		ElementType.METHOD,
		ElementType.PACKAGE,
		ElementType.TYPE,
})
@Deprecated(since = "1.4", forRemoval = true)
@Retention(RetentionPolicy.CLASS)
public @interface CheckReturnValue {}
