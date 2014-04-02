package demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.simple.SimpleRepositoryFactory;
import org.springframework.data.simple.SimpleRepositoryFactoryBean;

import demo.domain.Book;
import demo.domain.BookRepository;
import demo.domain.SimpleBookRepository;

@Configuration
@EnableAutoConfiguration
@Import(RepositoryRestMvcConfiguration.class)
public class Application {
	
	@Autowired
	protected DataSource dataSource;
	
	@Bean
	protected BookRepository repository() {
		return new SimpleBookRepository(dataSource);
	}

	@Bean
	public RepositoryFactoryBeanSupport<BookRepository, Book, Long> repositoryFactory() {
		SimpleRepositoryFactoryBean<BookRepository, Book, Long> factory = new SimpleRepositoryFactoryBean<BookRepository, Book, Long>() {
			@Override
			protected RepositoryFactorySupport createRepositoryFactory() {
				return new SimpleRepositoryFactory(repository());
			}
		};
		factory.setRepositoryInterface(BookRepository.class);
		return factory;
	}

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(Application.class, args);
    }
}
