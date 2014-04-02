package demo.domain;

import com.mysema.query.annotations.QueryEntity;


/**
 * Book is a Querydsl bean type
 */
@QueryEntity
public class Book {

    private Long id;

    private String title;

 	public Book(long id) {
 		this.id = id;
	}

	public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

