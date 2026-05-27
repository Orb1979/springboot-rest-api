package org.example.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String title;

	private String subTitle;

	private String description;

	private int pages;

	private String isbn;

	// Books.authers is the owning side, can only have 1 owning side, so Author.books is the inverse side
	// Book writes to author_books, Author reads from author_books
	@ManyToMany()
	@JoinTable(
			name = "author_book",
			joinColumns = @JoinColumn(name = "book_id"),
			inverseJoinColumns = @JoinColumn(name = "author_id")
	)
	private final List<Author> authors = new ArrayList<>();

	@ManyToOne()
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	// this can be a bit confusing:
	// @JoinColumn = for a property, where is the foreign key stored in the table?
	// @JoinTable = for a property, where is the join table
	//   joinColumns = FK to THIS entity
	//   inverseJoinColumns = FK to the OTHER entity

}
