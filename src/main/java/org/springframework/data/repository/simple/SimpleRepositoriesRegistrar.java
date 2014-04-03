/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.repository.simple;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.Repository;

/**
 * @author Dave Syer
 * 
 */
@Configuration
public class SimpleRepositoriesRegistrar implements BeanDefinitionRegistryPostProcessor {

	public static final String INFORMATION_SUFFIX = ".information";
	private BeanDefinitionRegistry registry;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				beanFactory, Repository.class, false, false)) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder
					.genericBeanDefinition(SimpleRepositoryFactoryInformation.class);
			builder.addConstructorArgReference(name);
			registry.registerBeanDefinition(name + INFORMATION_SUFFIX, builder.getBeanDefinition());
		}
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
			throws BeansException {
		this.registry = registry;
	}

}
