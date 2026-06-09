package org.example.api.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.AuthorDto;
import org.example.api.dto.BookRequestDto;
import org.example.api.dto.BookResponseDto;
import org.example.api.dto.PublisherDto;
import org.example.api.entity.Author;
import org.example.api.entity.Book;
import org.example.api.entity.Publisher;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.repository.AuthorRepository;
import org.example.api.repository.BookRepository;
import org.example.api.repository.PublisherRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
  private final BookRepository bookRepository;
  private final PublisherRepository publisherRepository;
  private final AuthorRepository authorRepository;

  public BookResponseDto getBook(UUID id) {
    Book book =
        bookRepository
            .findByIdHydrated(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found, id: " + id));
    return toResponseDto(book);
  }

  public List<BookResponseDto> getBooks() {
    return bookRepository.findAllHydrated().stream().map(this::toResponseDto).toList();
  }

  public BookResponseDto createBook(BookRequestDto bookRequestDto) {
    Book book = toEntity(bookRequestDto);
    Book saved = bookRepository.save(book);
    return toResponseDto(saved);
  }

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
    existingBook.getAuthors().clear();
    existingBook.getAuthors().addAll(resolveAuthors(bookRequestDto.authorIds()));

    Book saved = bookRepository.save(existingBook);
    return toResponseDto(saved);
  }

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
      existingBook.getAuthors().clear();
      existingBook.getAuthors().addAll(resolveAuthors(dto.authorIds()));
    }

    Book saved = bookRepository.save(existingBook);
    return toResponseDto(saved);
  }

  public void deleteBook(UUID id) {
    if (!bookRepository.existsById(id)) {
      throw new ResourceNotFoundException("Book not found, id: " + id);
    }
    bookRepository.deleteById(id);
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
        book.getAuthors().stream()
            .map(
                author ->
                    new AuthorDto(
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
    if (dto.id() != null) {
      book.setId(dto.id());
    }
    book.setTitle(dto.title());
    book.setSubTitle(dto.subTitle());
    book.setDescription(dto.description());
    book.setPages(dto.pages());
    book.setIsbn(dto.isbn());
    if (dto.publisherId() != null) {
      book.setPublisher(resolvePublisher(dto.publisherId()));
    }
    if (dto.authorIds() != null) {
      book.getAuthors().addAll(resolveAuthors(dto.authorIds()));
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
