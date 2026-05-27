package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookDto;
import org.example.api.entity.Book;
import org.example.api.entity.Publisher;
import org.example.api.repository.BookRepository;
import org.example.api.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;
	private final PublisherRepository publisherRepository;

	public BookDto getBook(UUID id) {
		Book book = bookRepository.findById(id).orElse(null);
		return toDto(book);
	}

	public List<BookDto> getBooks() {
		return bookRepository.findAll().stream().map(this::toDto).toList();
	}

	public BookDto createBook(BookDto bookDto) {
		Book book = toEntity(bookDto);
		Book saved = bookRepository.save(book);
		return toDto(saved);
	}

	public BookDto updateBook(UUID id, BookDto bookDto) {
		Book existingBook = bookRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

		existingBook.setTitle(bookDto.title());
		existingBook.setSubTitle(bookDto.subTitle());
		existingBook.setDescription(bookDto.description());
		existingBook.setPages(bookDto.pages());
		existingBook.setIsbn(bookDto.isbn());
		existingBook.setPublisher(resolvePublisher(bookDto.publisherId()));

		Book saved = bookRepository.save(existingBook);
		return toDto(saved);
	}

	public void deleteBook(UUID id) {
		if (!bookRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
		}
		bookRepository.deleteById(id);
	}

	private BookDto toDto(Book book) {
		if (book == null) {
			return null;
		}
		return new BookDto(
				book.getId(),
				book.getTitle(),
				book.getSubTitle(),
				book.getDescription(),
				book.getPages(),
				book.getIsbn(),
				book.getPublisher() != null ? book.getPublisher().getId() : null
		);
	}

	private Book toEntity(BookDto dto) {
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
		book.setPublisher(resolvePublisher(dto.publisherId()));
		return book;
	}

	private Publisher resolvePublisher(UUID publisherId) {
		if (publisherId == null) {
			return null;
		}
		return publisherRepository.findById(publisherId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));
	}
}

