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
package org.springframework.modulith.observability.support;

import io.micrometer.observation.ObservationRegistry;

import java.util.function.Supplier;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.RootResourceInformation;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.runtime.ApplicationModulesRuntime;
import org.springframework.util.Assert;

/**
 * @author Oliver Drotbohm
 * @author Marcin Grzejszczak
 */
public class SpringDataRestModuleObservabilityBeanPostProcessor extends ModuleObservabilitySupport
		implements BeanPostProcessor {

	private final ApplicationModulesRuntime runtime;
	private final Supplier<ObservationRegistry> observationRegistry;
	private final Environment environment;

	/**
	 * Creates a new {@link SpringDataRestModuleObservabilityBeanPostProcessor} for the given
	 * {@link ApplicationModulesRuntime} and {@link ObservationRegistry}.
	 *
	 * @param runtime must not be {@literal null}.
	 * @param observationRegistry must not be {@literal null}.
	 * @param environment must not be {@literal null}.
	 */
	public SpringDataRestModuleObservabilityBeanPostProcessor(ApplicationModulesRuntime runtime,
			Supplier<ObservationRegistry> observationRegistry, Environment environment) {

		Assert.notNull(runtime, "ApplicationModulesRuntime must not be null!");
		Assert.notNull(observationRegistry, "ObservationRegistry must not be null!");

		this.runtime = runtime;
		this.observationRegistry = observationRegistry;
		this.environment = environment;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		Class<?> type = runtime.getUserClass(bean, beanName);

		if (!AnnotatedElementUtils.hasAnnotation(type, BasePathAwareController.class)) {
			return bean;
		}

		var interceptor = new DataRestControllerInterceptor(runtime, observationRegistry, environment);
		var advisor = new DefaultPointcutAdvisor(interceptor);

		return addAdvisor(bean, advisor, it -> it.setProxyTargetClass(true));
	}

	private static class DataRestControllerInterceptor implements MethodInterceptor {

		private final Supplier<ApplicationModules> modules;
		private final Supplier<ObservationRegistry> observationRegistry;
		private final Environment environment;

		/**
		 * Creates a new {@link DataRestControllerInterceptor} for the given {@link ApplicationModules} and {@link Tracer}.
		 *
		 * @param modules must not be {@literal null}.
		 * @param observationRegistry must not be {@literal null}.
		 * @param environment must not be {@literal null}.
		 */
		private DataRestControllerInterceptor(Supplier<ApplicationModules> modules,
				Supplier<ObservationRegistry> observationRegistry,
				Environment environment) {

			Assert.notNull(modules, "ApplicationModules must not be null!");
			Assert.notNull(observationRegistry, "ObservationRegistry must not be null!");
			Assert.notNull(environment, "Environment must not be null!");

			this.modules = modules;
			this.observationRegistry = observationRegistry;
			this.environment = environment;
		}

		/*
		 * (non-Javadoc)
		 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
		 */
		@Override
		public @Nullable Object invoke(MethodInvocation invocation) throws Throwable {

			var module = getModuleFrom(invocation.getArguments());

			if (module == null) {
				return invocation.proceed();
			}

			var observed = new DefaultObservedModule(module);

			return ModuleEntryInterceptor.of(observed, observationRegistry.get(), environment).invoke(invocation);
		}

		@Nullable
		private ApplicationModule getModuleFrom(Object[] arguments) {

			for (Object argument : arguments) {

				if (!(argument instanceof RootResourceInformation info)) {
					continue;
				}

				return modules.get().getModuleByType(info.getDomainType().getName()).orElse(null);
			}

			return null;
		}
	}
}
