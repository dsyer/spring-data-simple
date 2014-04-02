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

package org.springframework.data.simple;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.Repository;

/**
 * @author Dave Syer
 *
 */
@Configuration
public class SimpleRepositoriesRegistrar<S> implements BeanPostProcessor {
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if (bean instanceof Repository) {
			SimpleRepositoryFactoryBean<Repository<S, Serializable>, S, Serializable> factory = new SimpleRepositoryFactoryBean<Repository<S,Serializable>, S, Serializable>((Repository<?, ?>) bean);
			factory.setRepositoryInterface(findRepositoryInterface(bean));
			return factory;
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	private Class<? extends Repository<S, Serializable>> findRepositoryInterface(
			Object bean) {
		for (Class<?> type : bean.getClass().getInterfaces()) {
			if (Repository.class.isAssignableFrom(type)) {
				@SuppressWarnings("unchecked")
				Class<? extends Repository<S, Serializable>> result = (Class<? extends Repository<S, Serializable>>) type;
				return result;
			}
		}
		throw new IllegalStateException("Could not find Repository interface for " + bean.getClass()); 
	}

}
