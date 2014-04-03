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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.util.ClassUtils;

/**
 * @author Dave Syer
 * 
 */
public class SimpleRepositoriesRegistrar implements ImportBeanDefinitionRegistrar,
		BeanDefinitionRegistryPostProcessor, ResourceLoaderAware, EnvironmentAware {

	private ResourceLoader resourceLoader;
	private Environment environment;

	public static final String INFORMATION_SUFFIX = ".information";
	private BeanDefinitionRegistry registry;
	
	private AnnotationRepositoryConfigurationSource configurationSource;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	public void setConfigurationSource(
			AnnotationRepositoryConfigurationSource configurationSource) {
		this.configurationSource = configurationSource;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
			BeanDefinitionRegistry registry) {
		AnnotationRepositoryConfigurationSource configurationSource = new AnnotationRepositoryConfigurationSource(
				annotationMetadata, EnableSimpleRepositories.class, resourceLoader,
				environment);
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.rootBeanDefinition(SimpleRepositoriesRegistrar.class);
		builder.addPropertyValue("configurationSource", configurationSource);
		registry.registerBeanDefinition(SimpleRepositoriesRegistrar.class.getName(),
				builder.getBeanDefinition());
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		for (String name : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				beanFactory, Repository.class, false, false)) {
			if (isIncluded(beanFactory.getType(name))) {
				BeanDefinitionBuilder builder = BeanDefinitionBuilder
						.genericBeanDefinition(SimpleRepositoryFactoryInformation.class);
				builder.addConstructorArgReference(name);
				registry.registerBeanDefinition(name + INFORMATION_SUFFIX,
						builder.getBeanDefinition());
			}
		}
	}

	private boolean isIncluded(Class<?> type) {
		for (String pkg : configurationSource.getBasePackages() ) {
			if (ClassUtils.getPackageName(type).startsWith(pkg)) {
				for (BeanDefinition definition : configurationSource.getCandidates(resourceLoader)) {
					if (ClassUtils.resolveClassName(definition.getBeanClassName(), null).isAssignableFrom(type)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
			throws BeansException {
		this.registry = registry;
	}

}
