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
import java.util.List;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.util.TypeInformation;

/**
 * @author Dave Syer
 * 
 */
public class SimpleRepositoryFactoryInformation<S, ID extends Serializable> implements
		RepositoryFactoryInformation<S, ID> {
	
	private SimpleRepositoryFactoryDelegate<Repository<S,ID>, S, ID> delegate;
	private Repository<S, ID> target;
		
	public SimpleRepositoryFactoryInformation(Repository<S,ID> target) {
		this.target = target;
	}
	
	@Override
	public List<QueryMethod> getQueryMethods() {
		return getDelegate().getQueryMethods();
	}

	@Override
	public EntityInformation<S, ID> getEntityInformation() {
		return getDelegate().getEntityInformation();
	}

	@Override
	public RepositoryInformation getRepositoryInformation() {
		return getDelegate().getRepositoryInformation();
	}

	private SimpleRepositoryFactoryDelegate<Repository<S, ID>, S, ID> getDelegate() {
		if (delegate==null) {
			delegate = new SimpleRepositoryFactoryDelegate<Repository<S, ID>, S, ID>(target);
			@SuppressWarnings("unchecked")
			Class<? extends Repository<S, ID>> repositoryInterface = (Class<? extends Repository<S, ID>>) findRepositoryInterface(target.getClass());
			delegate.setRepositoryInterface(repositoryInterface);
			delegate.afterPropertiesSet();
		}
		return delegate;
	}

	@Override
	public PersistentEntity<?, ?> getPersistentEntity() {
		return getDelegate().getPersistentEntity();
	}

	private static class SimpleRepositoryFactoryDelegate<T extends Repository<S, ID>, S, ID extends Serializable>
			extends RepositoryFactoryBeanSupport<T, S, ID> {

		private Repository<?, ?> target;

		public SimpleRepositoryFactoryDelegate(Repository<?, ?> target) {
			this.target = target;
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
			return new SimpleRepositoryFactory(target);
		}

	}

	private static <S> Class<? extends Repository<S, Serializable>> findRepositoryInterface(
			Class<?> beanClass) {
		for (Class<?> type : beanClass.getInterfaces()) {
			if (Repository.class.isAssignableFrom(type)) {
				@SuppressWarnings("unchecked")
				Class<? extends Repository<S, Serializable>> result = (Class<? extends Repository<S, Serializable>>) type;
				return result;
			}
		}
		throw new IllegalStateException("Could not find Repository interface for "
				+ beanClass);
	}

}
