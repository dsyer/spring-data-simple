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

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.Repository;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for simple Spring Data Repositories.
 *
 * @author Dave Syer
 * @see EnableSimpleRepositories
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(Repository.class)
@ConditionalOnMissingBean(SimpleRepositoryFactoryInformation.class)
@Import(SimpleRepositoriesAutoConfigureRegistrar.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class SimpleRepositoriesAutoConfiguration {

	@Configuration
	@EnableSpringDataWebSupport
	@ConditionalOnWebApplication
	@ConditionalOnMissingBean(PageableHandlerMethodArgumentResolver.class)
	protected static class SimpleWebConfiguration {

	}

}
