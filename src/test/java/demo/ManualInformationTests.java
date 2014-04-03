package demo;

import static org.junit.Assert.assertEquals;
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
import org.springframework.data.simple.SimpleRepositoryFactoryInformation;
import org.springframework.data.simple.SimpleRepositoryRestMvcConfiguration;
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
import demo.domain.QBook;
import demo.domain.SimpleBookRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ManualInformationTests {

	@Autowired
	private ConfigurableWebApplicationContext context;

	@Autowired
	private DataSource dataSource;

	private MockMvc mockMvc;
	
	@Test
	@Ignore("Just used to generate QBook")
	public void generateQueryClasses() throws SQLException {
		GenericExporter exporter = new GenericExporter();
		exporter.setTargetFolder(new File("src/test/java"));
		exporter.export(Book.class.getPackage());
	}

	@Test
	public void simpleDslQuery() throws Exception {
		QBook book = new QBook("books");
		QBook books = new QBook("books");
		com.mysema.query.sql.Configuration configuration = new com.mysema.query.sql.Configuration(new H2Templates());
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
	public void simpleQuery() throws Exception {
		mockMvc.perform(get("/books/search")).andExpect(status().isOk());
		mockMvc.perform(get("/books/search/findByTitleContains?title=E")).andExpect(status().isOk());
	}


	@Configuration
	@EnableAutoConfiguration
	@Import(SimpleRepositoryRestMvcConfiguration.class)
	protected static class Application {

		@Autowired
		protected DataSource dataSource;

		@Bean
		public SimpleRepositoryFactoryInformation<Book, Long> repositoryFactory() {
			SimpleRepositoryFactoryInformation<Book, Long> factory = new SimpleRepositoryFactoryInformation<Book, Long>(
					bookRepository());
			return factory;
		}

		@Bean
		public SimpleBookRepository bookRepository() {
			return new SimpleBookRepository(dataSource);
		}

		public static void main(String[] args) throws Exception {
			SpringApplication.run(Application.class, args);
		}
	}
	
}