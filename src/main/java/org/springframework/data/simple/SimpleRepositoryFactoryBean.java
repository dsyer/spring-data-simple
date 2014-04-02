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

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.util.TypeInformation;

/**
 * @author Dave Syer
 * 
 */
public class SimpleRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
		extends RepositoryFactoryBeanSupport<T, S, ID> implements BeanFactoryAware {

	private BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public SimpleRepositoryFactoryBean() {
		setMappingContext(new AbstractMappingContext<SimplePersistentEntity<?>, SimplePersistentProperty>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			protected <P> SimplePersistentEntity createPersistentEntity(
					TypeInformation<P> typeInformation) {
				return new SimplePersistentEntity(typeInformation);
			}

			@Override
			protected SimplePersistentProperty createPersistentProperty(Field field,
					PropertyDescriptor descriptor, SimplePersistentEntity<?> owner,
					SimpleTypeHolder simpleTypeHolder) {
				return new SimplePersistentProperty(field, descriptor, owner,
						simpleTypeHolder);
			}
		});

	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {
		Repository<?, ?> repository = (Repository<?, ?>) beanFactory
				.getBean(getObjectType());
		return new SimpleRepositoryFactory(repository);
	}

}
