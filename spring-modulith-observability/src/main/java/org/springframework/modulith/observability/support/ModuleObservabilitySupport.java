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

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.scheduling.annotation.AsyncAnnotationAdvisor;

/**
 * @author Oliver Drotbohm
 */
class ModuleObservabilitySupport implements BeanFactoryAware {

	private @Nullable AbstractAutoProxyCreator creator;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.creator = beanFactory.getBeanProvider(AbstractAutoProxyCreator.class).getIfAvailable();
	}

	protected final Object addAdvisor(Object bean, Advisor advisor) {
		return addAdvisor(bean, advisor, __ -> {});
	}

	protected final Object addAdvisor(Object bean, Advisor advisor, Consumer<ProxyFactory> customizer) {

		if (bean instanceof Advised advised) {

			advised.addAdvisor(asyncAdvisorIndex(advised) + 1, advisor);

			return bean;

		} else {

			var factory = new ProxyFactory(bean);

			if (creator != null) {
				factory.copyFrom(creator);
			}

			customizer.accept(factory);
			factory.addAdvisor(advisor);

			return factory.getProxy(bean.getClass().getClassLoader());
		}
	}

	private static int asyncAdvisorIndex(Advised advised) {

		var advisors = advised.getAdvisors();

		for (int i = 0; i < advised.getAdvisorCount(); i++) {

			if (advisors[i] instanceof AsyncAnnotationAdvisor) {
				return i;
			}
		}

		return -1;
	}
}
