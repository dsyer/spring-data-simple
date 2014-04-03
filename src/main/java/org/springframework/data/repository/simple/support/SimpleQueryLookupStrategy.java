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

import java.lang.reflect.Method;

import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.ReflectionUtils;

/**
 * @author Dave Syer
 * 
 */
public class SimpleQueryLookupStrategy implements QueryLookupStrategy {

	private SimpleRepositoryFactory support;

	public SimpleQueryLookupStrategy(SimpleRepositoryFactory support) {
		this.support = support;
	}

	@Override
	public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata,
			NamedQueries namedQueries) {
		final Method execute = method;
		final RepositoryMetadata repo = metadata;
		final Object target = support.getTargetRepository(metadata);
		if (target == null) {
			throw new IllegalStateException("Could not find repository for: " + method);
		}
		return new RepositoryQuery() {

			@Override
			public QueryMethod getQueryMethod() {
				return new QueryMethod(execute, repo);
			}

			@Override
			public Object execute(Object[] parameters) {
				return ReflectionUtils.invokeMethod(execute, target, parameters);
			}
		};
	}

}
