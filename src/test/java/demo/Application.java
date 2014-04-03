package demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.simple.SimpleRepositoryFactoryInformation;

import demo.domain.Book;
import demo.domain.SimpleBookRepository;

@Configuration
@EnableAutoConfiguration
@Import(RepositoryRestMvcConfiguration.class)
public class Application {

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
