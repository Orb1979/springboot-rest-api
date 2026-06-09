package org.example.api;

import org.example.api.entity.Author;
import org.example.api.entity.Book;
import org.example.api.entity.Publisher;
import org.example.api.repository.AuthorRepository;
import org.example.api.repository.BookRepository;
import org.example.api.repository.PublisherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
This test suite focuses exclusively on JPA Entity Mapping, Database Constraints, and Relationship Cardinality.
We are not using MockMvc because that would limit testing to only what is exposed in the controller endpoints

By default, the @DataJpaTest annotation wraps every single @Test method in its own SQL database transaction.
The moment the test method finishes execution (whether it passes or fails), Spring immediately issues a ROLLBACK
command to the database. Every row created during that specific test is completely erased.

It will use in memory db (testImplementation 'com.h2database:h2')
so we don't have to clear our local db each time to ensure correct results
*/

@DataJpaTest
@ActiveProfiles("test")
class RelationshipCardinalityJpaTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	@Nested
	@DisplayName("One-to-Many / Many-to-One Relationships (Publisher <-> Book)")
	class OneToManyPublisherBookTests {

		@Test
		@DisplayName("Should successfully persist a standalone book without a publisher")
		void shouldAllowBookWithoutPublisher() {
			// Arrange
			Book standaloneBook = new Book();
			standaloneBook.setTitle("The Hobbit");
			standaloneBook.setSubTitle("There and Back Again");
			standaloneBook.setIsbn("9780261103344");
			standaloneBook.setPages(310);

			// Act
			Book savedBook = bookRepository.save(standaloneBook);
			flushAndClearCache();

			// Assert
			Book foundBook = bookRepository.findById(savedBook.getId()).orElseThrow();
			assertNull(foundBook.getPublisher(), "Book should not have a publisher associated");
			assertEquals(1, bookRepository.count());
		}

		@Test
		@DisplayName("Should associate multiple books with a single publisher (Cardinality Verification)")
		void shouldAssociateMultipleBooksWithSamePublisher() {
			// Arrange
			Publisher publisher = new Publisher();
			publisher.setName("O'Reilly Media");
			publisher.setCountry("USA");
			entityManager.persist(publisher);

			Book book1 = new Book();
			book1.setTitle("Designing Data-Intensive Applications");
			book1.setSubTitle("The Big Ideas Behind Reliable, Scalable, and Maintainable Systems");
			book1.setIsbn("9781449373320");
			book1.setPages(611);
			book1.setPublisher(publisher); // Establish relation from Owning side

			Book book2 = new Book();
			book2.setTitle("High Performance Browser Networking");
			book2.setSubTitle("What every web developer should know about networking and browser performance");
			book2.setIsbn("9781449344764");
			book2.setPages(382);
			book2.setPublisher(publisher); // Establish relation from Owning side

			// Act
			bookRepository.saveAll(List.of(book1, book2));
			flushAndClearCache();

			// Assert
			Publisher foundPublisher = publisherRepository.findById(publisher.getId()).orElseThrow();

			// Your DDL maps this. Let's make sure the inverse collection catches it after session clear
			assertEquals(2, foundPublisher.getBooks().size(), "Publisher should be mapped to exactly 2 books");
			assertEquals(2, bookRepository.count());
			assertEquals(1, publisherRepository.count());
		}
	}

	@Nested
	@DisplayName("Many-to-Many Relationships (Author <-> Book)")
	class ManyToManyAuthorBookTests {

		@Test
		@DisplayName("Should allow an author to exist standalone without any associated books")
		void shouldAllowAuthorWithoutBooks() {
			// Arrange
			Author author = new Author();
			author.setFirstName("Ada");
			author.setLastName("Lovelace");
			author.setBirthDate(LocalDate.of(1815, 12, 10));

			// Act
			authorRepository.save(author);
			flushAndClearCache();

			// Assert
			Author foundAuthor = authorRepository.findById(author.getId()).orElseThrow();
			assertTrue(foundAuthor.getBooks().isEmpty(), "Author should have an empty book list");
			assertEquals(1, authorRepository.count());
			assertEquals(0, bookRepository.count());
		}

		@Test
		@DisplayName("Should successfully link two distinct authors to a single shared book")
		void shouldAssociateMultipleAuthorsWithSameBook() {
			// Arrange
			Author author1 = new Author();
			author1.setFirstName("Andrew");
			author1.setLastName("Hunt");
			entityManager.persist(author1);

			Author author2 = new Author();
			author2.setFirstName("David");
			author2.setLastName("Thomas");
			entityManager.persist(author2);

			Book book = new Book();
			book.setTitle("The Pragmatic Programmer");
			book.setSubTitle("Your Journey To Mastery");
			book.setIsbn("9780135957059");
			book.setPages(352);

			// Book is the owning side, so we must add authors to the book's collection
			book.getAuthors().add(author1);
			book.getAuthors().add(author2);

			// Act
			bookRepository.save(book);
			flushAndClearCache();

			// Assert
			Book foundBook = bookRepository.findById(book.getId()).orElseThrow();
			assertEquals(2, foundBook.getAuthors().size(), "Book should link to exactly 2 authors");

			// Checking inverse mapping syncs cleanly
			Author foundAuthor1 = authorRepository.findById(author1.getId()).orElseThrow();
			assertEquals(1, foundAuthor1.getBooks().size(), "Author 1 should map back to the book");
		}

		@Test
		@DisplayName("Should allow a book to be saved with an empty authors collection")
		void shouldAllowBookWithoutAuthors() {
			// Arrange
			Book anonymousBook = new Book();
			anonymousBook.setTitle("Bourne Identity");
			anonymousBook.setSubTitle("Anonymity Thriller");
			anonymousBook.setIsbn("9780345431011");

			// Act
			bookRepository.save(anonymousBook);
			flushAndClearCache();

			// Assert
			Book foundBook = bookRepository.findById(anonymousBook.getId()).orElseThrow();
			assertTrue(foundBook.getAuthors().isEmpty(), "Owning collection should be completely empty");
			assertEquals(1, bookRepository.count());
		}

		@Test
		@DisplayName("Should associate multiple distinct books with a single author")
		void shouldAssociateMultipleBooksWithSameAuthor() {
			// Arrange
			Author author = new Author();
			author.setFirstName("Robert");
			author.setLastName("Martin");
			entityManager.persist(author);

			Book book1 = new Book();
			book1.setTitle("Clean Code");
			book1.setSubTitle("A Handbook of Agile Software Craftsmanship");
			book1.setIsbn("9780132350884");
			book1.getAuthors().add(author); // Book owns relation

			Book book2 = new Book();
			book2.setTitle("Clean Architecture");
			book2.setSubTitle("A Craftsman's Guide to Software Structure and Design");
			book2.setIsbn("9780134494166");
			book2.getAuthors().add(author); // Book owns relation

			// Act
			bookRepository.saveAll(List.of(book1, book2));
			flushAndClearCache();

			// Assert
			Author foundAuthor = authorRepository.findById(author.getId()).orElseThrow();
			assertEquals(2, foundAuthor.getBooks().size(), "Inverse relationship side should load 2 books");
			assertEquals(2, bookRepository.count());
			assertEquals(1, authorRepository.count());
		}
	}

	/**
	 * Clears transactional contexts completely.
	 * This eliminates any false tracking positives and guarantees Hibernate runs
	 * authentic 'SELECT' statements against the test database for assertions.
	 */
	private void flushAndClearCache() {
		entityManager.flush();
		entityManager.clear();
	}
}