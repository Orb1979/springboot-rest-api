package org.example.api.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.AuthorResponseDto;
import org.example.api.dto.BookRequestDto;
import org.example.api.dto.BookResponseDto;
import org.example.api.dto.PublisherDto;
import org.example.api.entity.Author;
import org.example.api.entity.AuthorBook;
import org.example.api.entity.Book;
import org.example.api.entity.Publisher;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.repository.AuthorRepository;
import org.example.api.repository.BookRepository;
import org.example.api.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
To the fewest database queries while still returning the full, richly populated BookResponseDto,
you should use the Fetch-after-Mutation pattern wrapped in a clear transaction boundary.
This gives you the best of both worlds:
- Efficient Writes: Hibernate loads only what it needs to perform a highly targeted, lightweight INSERT or DELETE on the join table.
- Efficient Reads: Instead of letting the DTO mapper lazily trigger unstructured, hidden N+1 queries, you explicitly
  use your existing optimized findByIdHydrated method to fetch the final state in one single, clean query block
*/

@Service
@RequiredArgsConstructor
public class BookServiceV2 {
  private final BookRepository bookRepository;
  private final PublisherRepository publisherRepository;
  private final AuthorRepository authorRepository;

  @Transactional(readOnly = true)
  public BookResponseDto getBook(UUID id) {
    Book book =
        bookRepository
            .findByIdHydrated(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found, id: " + id));
    return toResponseDto(book);
  }

  @Transactional(readOnly = true)
  public List<BookResponseDto> getBooks() {
    return bookRepository.findAllHydrated().stream().map(this::toResponseDto).toList();
  }

  @Transactional
  public BookResponseDto createBook(BookRequestDto bookRequestDto) {
    Book book = toEntity(bookRequestDto);
    Book saved = bookRepository.save(book);
    return toResponseDto(saved);
  }

  @Transactional
  public BookResponseDto updateBook(UUID id, BookRequestDto bookRequestDto) {
    Book existingBook =
        bookRepository
            .findByIdHydrated(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found, id: " + id));
    existingBook.setTitle(bookRequestDto.title());
    existingBook.setSubTitle(bookRequestDto.subTitle());
    existingBook.setDescription(bookRequestDto.description());
    existingBook.setPages(bookRequestDto.pages());
    existingBook.setIsbn(bookRequestDto.isbn());
    existingBook.setPublisher(resolvePublisher(bookRequestDto.publisherId()));
    existingBook.getAuthorBooks().clear();
    if (bookRequestDto.authorIds() != null) {
      resolveAuthors(bookRequestDto.authorIds()).forEach(author -> {
        existingBook.getAuthorBooks().add(new AuthorBook(author, existingBook));
      });
    }

    Book saved = bookRepository.save(existingBook);
    return toResponseDto(saved);
  }

  @Transactional
  public BookResponseDto patchBook(UUID id, BookRequestDto dto) {
    Book existingBook =
        bookRepository
            .findByIdHydrated(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found, id: " + id));

    if (dto.title() != null) {
      existingBook.setTitle(dto.title());
    }
    if (dto.subTitle() != null) {
      existingBook.setSubTitle(dto.subTitle());
    }
    if (dto.description() != null) {
      existingBook.setDescription(dto.description());
    }
    if (dto.pages() != null) {
      existingBook.setPages(dto.pages());
    }
    if (dto.isbn() != null) {
      existingBook.setIsbn(dto.isbn());
    }
    if (dto.publisherId() != null) {
      existingBook.setPublisher(resolvePublisher(dto.publisherId()));
    }
    if (dto.authorIds() != null) {
      existingBook.getAuthorBooks().clear();
      resolveAuthors(dto.authorIds()).forEach(author -> {
        existingBook.getAuthorBooks().add(new AuthorBook(author, existingBook));
      });
    }

    Book saved = bookRepository.save(existingBook);
    return toResponseDto(saved);
  }

  @Transactional
  public void deleteBook(UUID id) {
    if (!bookRepository.existsById(id)) {
      throw new ResourceNotFoundException("Book not found, id: " + id);
    }
    bookRepository.deleteById(id);
  }

  @Transactional
  public BookResponseDto addAuthorToBook(UUID bookId, UUID authorId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    Author author =
        authorRepository
            .findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

    boolean exists = book.getAuthorBooks().stream()
        .anyMatch(ab -> ab.getAuthor().getId().equals(authorId));
    if (!exists) {
      book.getAuthorBooks().add(new AuthorBook(author, book));
      bookRepository.save(book);
    }
    return getBook(bookId);
  }

  @Transactional
  public BookResponseDto removeAuthorFromBook(UUID bookId, UUID authorId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    book.getAuthorBooks().removeIf(ab -> ab.getAuthor().getId().equals(authorId));
    bookRepository.save(book);
    return getBook(bookId);
  }

  @Transactional
  public BookResponseDto replaceAuthors(UUID bookId, List<UUID> authorIds) {
    Book book =
        bookRepository
            .findByIdHydrated(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    List<Author> authors = resolveAuthors(authorIds);
    book.getAuthorBooks().clear();
    authors.forEach(author -> {
      book.getAuthorBooks().add(new AuthorBook(author, book));
    });

    Book saved = bookRepository.save(book);
    return toResponseDto(saved);
  }

  @Transactional
  public BookResponseDto removePublisherFromBook(UUID bookId) {
    Book book =
        bookRepository
            .findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    book.setPublisher(null);
    bookRepository.save(book);
    return getBook(bookId);
  }

  private BookResponseDto toResponseDto(Book book) {
    return new BookResponseDto(
        book.getId(),
        book.getTitle(),
        book.getSubTitle(),
        book.getDescription(),
        book.getPages(),
        book.getIsbn(),
        book.getPublisher() != null
            ? new PublisherDto(
                book.getPublisher().getId(),
                book.getPublisher().getName(),
                book.getPublisher().getCountry())
            : null,
        book.getAuthorBooks().stream()
            .map(AuthorBook::getAuthor)
            .map(
                author ->
                    new AuthorResponseDto(
                        author.getId(),
                        author.getFirstName(),
                        author.getLastName(),
                        author.getBirthDate()))
            .toList());
  }

  private Book toEntity(BookRequestDto dto) {
    if (dto == null) {
      return null;
    }

    Book book = new Book();
    book.setTitle(dto.title());
    book.setSubTitle(dto.subTitle());
    book.setDescription(dto.description());
    book.setPages(dto.pages());
    book.setIsbn(dto.isbn());
    if (dto.publisherId() != null) {
      book.setPublisher(resolvePublisher(dto.publisherId()));
    }
    if (dto.authorIds() != null) {
      resolveAuthors(dto.authorIds()).forEach(author -> {
        book.getAuthorBooks().add(new AuthorBook(author, book));
      });
    }
    return book;
  }

  private Publisher resolvePublisher(UUID publisherId) {
    if (publisherId == null) {
      return null;
    }
    return publisherRepository
        .findById(publisherId)
        .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));
  }

  private List<Author> resolveAuthors(List<UUID> authorIds) {
    List<Author> authors = authorRepository.findAllById(authorIds);
    if (authors.size() != authorIds.size()) {
      throw new ResourceNotFoundException("One or more authors not found");
    }
    return authors;
  }
}
