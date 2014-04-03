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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.util.ReflectionUtils;

import reactor.util.Assert;

/**
 * @author Dave Syer
 * 
 */
@Configuration
public class SimpleRepositoryRestMvcConfiguration extends RepositoryRestMvcConfiguration {

	@Autowired
	private ListableBeanFactory beanFactory;

	@Bean
	@Override
	public Repositories repositories() {
		Repositories repositories = super.repositories();
		fixRepositoryBeanNames(beanFactory, repositories);
		return repositories;
	}

	/**
	 * Hack to extract private map field from Repositories. The vanilla Repositories
	 * assumes that all RepositoryFactoryInformation instances are also FactoryBeans for
	 * the corresponding repository. That isn't the case here, so we need to point it back
	 * at the real target repository.
	 * 
	 * @param repositories the repositories instance
	 * @return the repositoryBeanNames
	 */
	private void fixRepositoryBeanNames(ListableBeanFactory beanFactory,
			Repositories repositories) {
		Field field = ReflectionUtils
				.findField(Repositories.class, "repositoryBeanNames");
		ReflectionUtils.makeAccessible(field);
		@SuppressWarnings("unchecked")
		Map<Class<?>, String> repositoryBeanNames = (Map<Class<?>, String>) ReflectionUtils
				.getField(field, repositories);
		for (Class<?> type : repositoryBeanNames.keySet()) {
			String name = repositoryBeanNames.get(type);
			name = findRepositoryBeanName(beanFactory, name);
			repositoryBeanNames.put(type, name);
		}
	}

	private String findRepositoryBeanName(ListableBeanFactory beanFactory, String name) {
		if (name.endsWith(SimpleRepositoriesRegistrar.INFORMATION_SUFFIX)) {
			name = name.replace(SimpleRepositoriesRegistrar.INFORMATION_SUFFIX, "");
		} else {
			if (beanFactory.getType(name).equals(SimpleRepositoryFactoryInformation.class)) {
				SimpleRepositoryFactoryInformation<?,?> bean = beanFactory.getBean(name, SimpleRepositoryFactoryInformation.class);
				Class<?> repositoryInterface = bean.getRepositoryInformation().getRepositoryInterface();
				String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory,repositoryInterface, false, false);
				Assert.state(names.length==1, "Expected only one bean of type: " + repositoryInterface + ", but found " + Arrays.asList(names));
				name = names[0];
			}
		}
		return name;
	}
}
