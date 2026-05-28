package org.example.api.service;

import org.example.api.dto.BookDto;
import org.example.api.entity.Book;
import org.example.api.entity.Publisher;
import org.example.api.repository.BookRepository;
import org.example.api.repository.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private PublisherRepository publisherRepository;

	@InjectMocks
	private BookService bookService;

	@Test
	void createBookSavesAndReturnsBook() {
		UUID id = UUID.randomUUID();
		UUID publisherId = UUID.randomUUID();

		Publisher publisher = new Publisher();
		publisher.setId(publisherId);
		publisher.setName("example publisher");

		BookDto bookDto = new BookDto(id, "title", "subTitle", "description", 100, "isbn", publisherId);
		Book book = new Book(id, "title", "subTitle", "description", 100, "isbn", publisher);

		when(bookRepository.save(any(Book.class))).thenReturn(book);
		when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));

		BookDto result = bookService.createBook(bookDto);

		assertNotNull(result);
		assertEquals(id, result.id());
		assertEquals("title", result.title());
		assertEquals("subTitle", result.subTitle());
		assertEquals("description", result.description());
		assertEquals(100, result.pages());
		assertEquals("isbn", result.isbn());
		assertEquals(publisherId, result.publisherId());
	}

	@Test
	void updateBookUpdatesAndSavesExistingBook() {
		UUID id = UUID.randomUUID();
		UUID publisherId = UUID.randomUUID();
		UUID updatedPublisherId = UUID.randomUUID();

		Publisher existingPublisher = new Publisher();
		existingPublisher.setId(publisherId);
		existingPublisher.setName("old publisher");

		Publisher updatedPublisher = new Publisher();
		updatedPublisher.setId(updatedPublisherId);
		updatedPublisher.setName("new publisher");

		Book existingBook = new Book();
		existingBook.setId(id);
		existingBook.setTitle("Old Title");
		existingBook.setSubTitle("Old SubTitle");
		existingBook.setDescription("Old Description");
		existingBook.setPages(10);
		existingBook.setIsbn("old-isbn");
		existingBook.setPublisher(existingPublisher);

		BookDto updatedBook = new BookDto(null, "New Title", "New SubTitle", "New Description", 200, "new-isbn", updatedPublisherId);

		when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));
		when(publisherRepository.findById(updatedPublisherId)).thenReturn(Optional.of(updatedPublisher));
		when(bookRepository.save(existingBook)).thenReturn(existingBook);

		BookDto result = bookService.updateBook(id, updatedBook);

		assertEquals("New Title", result.title());
		assertEquals("New SubTitle", result.subTitle());
		assertEquals("New Description", result.description());
		assertEquals(200, result.pages());
		assertEquals("new-isbn", result.isbn());
		assertEquals(updatedPublisherId, result.publisherId());
		verify(bookRepository).save(existingBook);

	}

	@Test
	void updateBookThrowsWhenBookNotFound() {
		UUID id = UUID.randomUUID();
		UUID publisherId = UUID.randomUUID();
		BookDto bookDto = new BookDto(null, "title", "subTitle", "description", 100, "isbn", publisherId);

		when(bookRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResponseStatusException.class, () -> bookService.updateBook(id, bookDto));
		verify(bookRepository, never()).save(any());
	}

	@Test
	void deleteBookDeletesWhenFound() {
		UUID id = UUID.randomUUID();
		when(bookRepository.existsById(id)).thenReturn(true);

		bookService.deleteBook(id);

		verify(bookRepository).deleteById(id);
	}

	@Test
	void deleteBookThrowsWhenNotFound() {
		UUID id = UUID.randomUUID();
		when(bookRepository.existsById(id)).thenReturn(false);

		assertThrows(ResponseStatusException.class, () -> bookService.deleteBook(id));
		verify(bookRepository, never()).deleteById(any());
	}
}