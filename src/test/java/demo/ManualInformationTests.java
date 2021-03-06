package demo;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.simple.support.SimpleRepositoryFactoryBean;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.mysema.query.Tuple;
import com.mysema.query.codegen.GenericExporter;
import com.mysema.query.sql.H2Templates;
import com.mysema.query.sql.SQLQueryFactory;

import demo.ManualInformationTests.Application;
import demo.domain.Book;
import demo.domain.BookRepository;
import demo.domain.BookRepositoryImpl;
import demo.domain.QBook;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ManualInformationTests {

	@Autowired
	private ConfigurableWebApplicationContext context;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private BookRepository repository;

	private MockMvc mockMvc;

	@Test
	@Ignore("Just used to generate QBook")
	public void generateQueryClasses() throws SQLException {
		GenericExporter exporter = new GenericExporter();
		exporter.setTargetFolder(new File("src/test/java"));
		exporter.export(Book.class.getPackage());
	}
	
	@Test
	public void findOne() throws Exception {
		assertNotNull(repository.findOne(0L));
	}

	@Test
	public void simpleDslQuery() throws Exception {
		QBook book = new QBook("books");
		QBook books = new QBook("books");
		com.mysema.query.sql.Configuration configuration = new com.mysema.query.sql.Configuration(
				new H2Templates());
		List<Tuple> query = new SQLQueryFactory(configuration, dataSource).from(books)
				.where(book.title.startsWith("E")).list(books.id, books.title);
		assertEquals(1, query.size());
	}

	@Before
	public void init() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void homePage() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
	}

	@Test
	public void simpleRequest() throws Exception {
		mockMvc.perform(get("/books/search")).andExpect(status().isOk());
		mockMvc.perform(get("/books/search/findByTitleContains?title=E")).andExpect(
				status().isOk());
	}

	@Configuration
	@EnableAutoConfiguration
	@Import(RepositoryRestMvcConfiguration.class)
	protected static class Application {
		
		@Autowired
		private DataSource dataSource;

		@Bean
		public SimpleRepositoryFactoryBean<BookRepository, Book, Long> bookRepository() {
			SimpleRepositoryFactoryBean<BookRepository, Book, Long> factory = new SimpleRepositoryFactoryBean<BookRepository, Book, Long>();
			factory.setRepositoryInterface(BookRepository.class);
			factory.setCustomImplementation(new BookRepositoryImpl(dataSource));
			return factory;
		}

		public static void main(String[] args) throws Exception {
			SpringApplication.run(Application.class, args);
		}
	}

}