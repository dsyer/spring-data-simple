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

package demo.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Dave Syer
 *
 */
@Repository
public class SimpleBookRepository implements BookRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public SimpleBookRepository(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Iterable<Book> findByTitleContains(String value) {
		value = value.replace("'", "");
		return jdbcTemplate.query("SELECT id,title FROM BOOKS where title like '%" + value + "%'", new BookMapper());
	}
	
	@Override
	public Iterable<Book> findAll() {
		return jdbcTemplate.query("SELECT id,title FROM BOOKS", new BookMapper());
	}
	
	private static class BookMapper implements RowMapper<Book> {

		@Override
		public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
			Book book = new Book(rs.getLong(1));
			book.setTitle(rs.getString(2));
			return book;
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends Book> S save(S entity) {
		if (findOne(entity.getId())!=null) {
			jdbcTemplate.update("UPDATE BOOKS set title=? where id=?", entity.getTitle(), entity.getId());
		}
		Book result = new Book(jdbcTemplate.queryForObject("SELECT max(id) from BOOKS", Long.class) + 1 );
		result.setTitle(entity.getTitle());
		jdbcTemplate.update("INSERT into BOOKS (id,title) values (?,?)", result .getId(), result.getTitle());
		return (S) result;
	}

	@Override
	public <S extends Book> Iterable<S> save(Iterable<S> entities) {
		List<S> result = new ArrayList<S>();
		for (S book : entities) {
			result.add(save(book));
		}
		return result;
	}

	@Override
	public Book findOne(Long id) {
		return jdbcTemplate.queryForObject("SELECT id, title, from BOOKS where id=?", new BookMapper());
	}

	@Override
	public boolean exists(Long id) {
		return findOne(id)!=null;
	}

	@Override
	public Iterable<Book> findAll(Iterable<Long> ids) {
		List<Book> result = new ArrayList<Book>();
		for (Long id : ids) {
			result.add(findOne(id));
		}
		return result;
	}

	@Override
	public long count() {
		return jdbcTemplate.queryForObject("SELECT count(id) from BOOKS", Long.class);
	}

	@Override
	public void delete(Long id) {
		jdbcTemplate.update("DELETE from BOOK WHERE id=?", id);
	}

	@Override
	public void delete(Book entity) {
		delete(entity.getId());
	}

	@Override
	public void delete(Iterable<? extends Book> entities) {
		for (Book book : entities) {
			delete(book.getId());
		}
	}

	@Override
	public void deleteAll() {
		for (Book book : findAll()) {
			delete(book.getId());
		}
	}

}
