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

package org.springframework.data.repository.simple.support;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AbstractPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;

/**
 * @author Dave Syer
 * 
 */
public class SimplePersistentProperty extends
		AbstractPersistentProperty<SimplePersistentProperty> {

	public SimplePersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
			PersistentEntity<?, SimplePersistentProperty> owner,
			SimpleTypeHolder simpleTypeHolder) {
		super(field, propertyDescriptor, owner, simpleTypeHolder);
	}

	@Override
	public boolean isIdProperty() {
		return getName().equals("id");
	}

	@Override
	public boolean isVersionProperty() {
		return getName().equals("version");
	}

	@Override
	public <A extends Annotation> A findAnnotation(Class<A> annotationType) {
		return field.getAnnotation(annotationType);
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
		return findAnnotation(annotationType) != null;
	}

	@Override
	protected Association<SimplePersistentProperty> createAssociation() {
		return new Association<SimplePersistentProperty>(this, null);
	}

}
