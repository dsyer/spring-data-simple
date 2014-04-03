package demo;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.simple.EnableSimpleRepositories;
import org.springframework.data.repository.simple.SimpleRepositoryRestMvcConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import demo.ScanningTests.ScanningApplication;
import demo.domain.BookRepository;
import demo.domain.SimpleBookRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ScanningApplication.class)
@WebAppConfiguration
@ActiveProfiles("scan")
public class ScanningTests {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private ConfigurableWebApplicationContext context;

	private MockMvc mockMvc;

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
		mockMvc.perform(get("/books/search/findByTitleContains?title=E")).andExpect(status().isOk());
	}

	@Test
	public void simpleQuery() throws Exception {
		assertTrue(bookRepository.findAll().iterator().hasNext());
	}
	
	@Configuration
	@EnableSimpleRepositories(basePackageClasses=ScanningApplication.class)
	@EnableAutoConfiguration
	@Import(SimpleRepositoryRestMvcConfiguration.class)
	protected static class ScanningApplication {
		
		@Autowired
		private DataSource dataSource;

		@Bean
		public SimpleBookRepository bookRepository() {
			return new SimpleBookRepository(dataSource);
		}
		
	}

}