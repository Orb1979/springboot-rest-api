package org.example.api.web;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookDto;
import org.example.api.service.BookService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/book")
public class BookController {

	private final BookService bookService;

	@GetMapping("/{id}")
	public BookDto getBook(@PathVariable UUID id) {
		return bookService.getBook(id);
	}

	@GetMapping
	public List<BookDto> getBooks() {
		return bookService.getBooks();
	}

	@PostMapping
	public BookDto createBook(@RequestBody BookDto bookDto) {
		return bookService.createBook(bookDto);
	}

	@PutMapping("/{id}")
	public BookDto updateBook(@PathVariable UUID id, @RequestBody BookDto bookDto) {
		return bookService.updateBook(id, bookDto);
	}

	@DeleteMapping("/{id}")
	public void deleteBook(@PathVariable UUID id) {
		bookService.deleteBook(id);
	}
}

