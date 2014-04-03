package demo;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.mysema.query.Tuple;
import com.mysema.query.codegen.GenericExporter;
import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.H2Templates;
import com.mysema.query.sql.SQLQueryFactory;

import demo.domain.Book;
import demo.domain.QBook;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Test
	@Ignore("Just used to generate QBook")
	public void generateQueryClasses() throws SQLException {
		GenericExporter exporter = new GenericExporter();
		exporter.setTargetFolder(new File("src/test/java"));
		exporter.export(Book.class.getPackage());
	}

	@Test
	public void simpleQuery() throws Exception {
		QBook book = new QBook("books");
		QBook books = new QBook("books");
		Configuration configuration = new Configuration(new H2Templates());
		List<Tuple> query = new SQLQueryFactory(configuration, dataSource).from(books)
				.where(book.title.startsWith("E")).list(books.id, books.title);
		assertEquals(1, query.size());
	}

}