package org.example.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.api.dto.BookRequestDto;
import org.example.api.dto.BookResponseDto;
import org.example.api.entity.Author;
import org.example.api.entity.AuthorBook;
import org.example.api.entity.Book;
import org.example.api.entity.Publisher;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.repository.AuthorRepository;
import org.example.api.repository.BookRepository;
import org.example.api.repository.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceV2Test {

  @Mock private BookRepository bookRepository;

  @Mock private PublisherRepository publisherRepository;

  @Mock private AuthorRepository authorRepository;

  @InjectMocks private BookServiceV2 bookService;

  @Test
  void createBookWithoutPublisherAndAuthor() {
    UUID bookId = UUID.randomUUID();

    BookRequestDto request =
        new BookRequestDto( "title", "subtitle", "description", 100, "isbn", null, null);

    when(bookRepository.save(any(Book.class)))
        .thenAnswer(
            invocation -> {
              Book savedBook = invocation.getArgument(0);
              savedBook.setId(bookId);
              return savedBook;
            });

    BookResponseDto result = bookService.createBook(request);
    assertNotNull(result);
    assertEquals(bookId, result.id());
    assertNull(result.publisher());
    assertEquals(0, result.authors().size());
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void createBookWithPublisherAndAuthors() {
    UUID bookId = UUID.randomUUID();
    UUID publisherId = UUID.randomUUID();
    UUID author1Id = UUID.randomUUID();
    UUID author2Id = UUID.randomUUID();

    Publisher publisher = new Publisher();
    publisher.setId(publisherId);

    Author author1 = new Author();
    author1.setId(author1Id);

    Author author2 = new Author();
    author2.setId(author2Id);

    BookRequestDto request =
        new BookRequestDto(
            "title",
            "subtitle",
            "description",
            100,
            "isbn",
            publisherId,
            List.of(author1Id, author2Id));

    when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
    when(authorRepository.findAllById(List.of(author1Id, author2Id)))
        .thenReturn(List.of(author1, author2));
    when(bookRepository.save(any(Book.class)))
        .thenAnswer(
            invocation -> {
              Book savedBook = invocation.getArgument(0);
              savedBook.setId(bookId);
              return savedBook;
            });

    BookResponseDto result = bookService.createBook(request);
    assertNotNull(result);
    assertEquals(bookId, result.id());
    assertEquals(publisherId, result.publisher().id());
    assertEquals(2, result.authors().size());
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void createBookThrowsWhenAuthorNotFound() {
    UUID publisherId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Publisher publisher = new Publisher();
    publisher.setId(publisherId);

    BookRequestDto request =
        new BookRequestDto("title", "subtitle", "description", 100, "isbn", publisherId, List.of(authorId));

    when(publisherRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
    when(authorRepository.findAllById(List.of(authorId))).thenReturn(List.of());

    assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(request));
    verify(bookRepository, never()).save(any());
  }

  @Test
  void createBookThrowsWhenPublisherNotFound() {
    UUID publisherId = UUID.randomUUID();

    BookRequestDto request =
        new BookRequestDto("title", "subtitle", "description", 100, "isbn", publisherId, List.of());

    when(publisherRepository.findById(publisherId)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(request));
    verify(bookRepository, never()).save(any());
  }

  @Test
  void patchBook() {
    UUID bookId = UUID.randomUUID();

    Book existingBook = new Book();
    existingBook.setId(bookId);
    existingBook.setTitle("old title");
    existingBook.setSubTitle("old subTitle");

    BookRequestDto request = new BookRequestDto("new title", null, null, null, null, null, null);

    when(bookRepository.findByIdHydrated(bookId)).thenReturn(Optional.of(existingBook));
    when(bookRepository.save(existingBook)).thenReturn(existingBook);

    BookResponseDto result = bookService.patchBook(bookId, request);
    assertEquals("new title", result.title());
    assertEquals("old subTitle", result.subTitle());
    verify(bookRepository).save(existingBook);
  }

  @Test
  void updateBook() {
    UUID bookId = UUID.randomUUID();
    UUID oldPublisherId = UUID.randomUUID();
    UUID newPublisherId = UUID.randomUUID();
    UUID oldAuthorId = UUID.randomUUID();
    UUID newAuthorId = UUID.randomUUID();

    Publisher oldPublisher = new Publisher();
    oldPublisher.setId(oldPublisherId);

    Publisher newPublisher = new Publisher();
    newPublisher.setId(newPublisherId);

    Author oldAuthor = new Author();
    oldAuthor.setId(oldAuthorId);

    Author newAuthor = new Author();
    newAuthor.setId(newAuthorId);

    Book existingBook = new Book();
    existingBook.setId(bookId);
    existingBook.setPublisher(oldPublisher);
    existingBook.getAuthorBooks().add(new AuthorBook(oldAuthor, existingBook));

    BookRequestDto request =
        new BookRequestDto(
            "New Title",
            "New Subtitle",
            "New Description",
            200,
            "new-isbn",
            newPublisherId,
            List.of(newAuthorId));

    when(bookRepository.findByIdHydrated(bookId)).thenReturn(Optional.of(existingBook));
    when(publisherRepository.findById(newPublisherId)).thenReturn(Optional.of(newPublisher));
    when(authorRepository.findAllById(List.of(newAuthorId))).thenReturn(List.of(newAuthor));
    when(bookRepository.save(existingBook)).thenReturn(existingBook);

    BookResponseDto result = bookService.updateBook(bookId, request);
    assertEquals("New Title", result.title());
    assertEquals(newPublisherId, result.publisher().id());
    assertEquals(1, result.authors().size());
    assertEquals(newAuthorId, result.authors().getFirst().id());
    verify(bookRepository).save(existingBook);
  }

  @Test
  void updateBookThrowsWhenBookNotFound() {
    UUID id = UUID.randomUUID();
    UUID publisherId = UUID.randomUUID();
    BookRequestDto bookRequestDto =
        new BookRequestDto("title", "subTitle", "description", 100, "isbn", publisherId, List.of());
    when(bookRepository.findByIdHydrated(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(id, bookRequestDto));
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
    assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(id));
    verify(bookRepository, never()).deleteById(any());
  }

  // --- Granular Relationship Sub-Resource Tests ---

  @Test
  void addAuthorToBook() {
    UUID bookId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Book book = new Book();
    book.setId(bookId);

    Author author = new Author();
    author.setId(authorId);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(bookRepository.findByIdHydrated(bookId)).thenReturn(Optional.of(book));

    BookResponseDto result = bookService.addAuthorToBook(bookId, authorId);

    assertNotNull(result);
    verify(bookRepository).save(book);
    verify(bookRepository).findByIdHydrated(bookId);
  }

  @Test
  void removeAuthorFromBook() {
    UUID bookId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Author author = new Author();
    author.setId(authorId);

    Book book = new Book();
    book.setId(bookId);
    book.getAuthorBooks().add(new AuthorBook(author, book));

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(bookRepository.findByIdHydrated(bookId)).thenReturn(Optional.of(book));

    BookResponseDto result = bookService.removeAuthorFromBook(bookId, authorId);

    assertNotNull(result);
    assertTrue(book.getAuthorBooks().isEmpty());
    verify(bookRepository).save(book);
    verify(bookRepository).findByIdHydrated(bookId);
  }

  @Test
  void replaceAuthors() {
    UUID bookId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    Author newAuthor = new Author();
    newAuthor.setId(authorId);

    Book book = new Book();
    book.setId(bookId);

    // Since replaceAuthors handles complete collection overwrite, it starts hydrated
    when(bookRepository.findByIdHydrated(bookId)).thenReturn(Optional.of(book));
    when(authorRepository.findAllById(List.of(authorId))).thenReturn(List.of(newAuthor));
    when(bookRepository.save(book)).thenReturn(book);

    BookResponseDto result = bookService.replaceAuthors(bookId, List.of(authorId));

    assertNotNull(result);
    assertEquals(1, result.authors().size());
    assertEquals(authorId, result.authors().getFirst().id());
    verify(bookRepository).save(book);
  }

  @Test
  void removePublisherFromBook() {
    UUID bookId = UUID.randomUUID();
    Publisher publisher = new Publisher();
    publisher.setId(UUID.randomUUID());

    Book book = new Book();
    book.setId(bookId);
    book.setPublisher(publisher);

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(bookRepository.findByIdHydrated(bookId)).thenReturn(Optional.of(book));

    BookResponseDto result = bookService.removePublisherFromBook(bookId);

    assertNotNull(result);
    assertNull(book.getPublisher());
    verify(bookRepository).save(book);
    verify(bookRepository).findByIdHydrated(bookId);
  }
}
