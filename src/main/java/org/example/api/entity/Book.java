package org.example.api.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

  // Books.authers is the owning side, can only have 1 owning side, so Author.books is the inverse
  // side
  // Book writes to author_books, Author reads from author_books
  @ManyToMany()
  @JoinTable(
      name = "author_book",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "author_id"))
  private final List<Author> authors = new ArrayList<>();

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String title;
  private String subTitle;
  private String description;
  private int pages;
  private String isbn;

  @ManyToOne()
  @JoinColumn(name = "publisher_id")
  private Publisher publisher;

  // this can be a bit confusing:
  // @JoinColumn = for a property, where is the foreign key stored in the table?
  // @JoinTable = for a property, where is the join table
  //   joinColumns = FK to THIS entity
  //   inverseJoinColumns = FK to the OTHER entity

}
