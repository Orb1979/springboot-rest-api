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

	// curl -X GET localhost:8080/api/v1/book/{id}
	@GetMapping("/{id}")
	public BookDto getBook(@PathVariable UUID id) {
		return bookService.getBook(id);
	}

	// curl -X GET localhost:8080/api/v1/book
	@GetMapping
	public List<BookDto> getBooks() {
		return bookService.getBooks();
	}

	// curl -X POST localhost:8080/api/v1/book -H "Content-Type: application/json" -d '{"title":"Clean Code","subTitle":"A Handbook of Agile Software Craftsmanship","description":"Classic software engineering book","pages":464,"isbn":"9780132350884","publisherId":"00000000-0000-0000-0000-000000000000"}'
	@PostMapping
	public BookDto createBook(@RequestBody BookDto bookDto) {
		return bookService.createBook(bookDto);
	}

	// curl -X PUT localhost:8080/api/v1/book/{id} -H "Content-Type: application/json" -d '{"title":"Clean Architecture","subTitle":"A Craftsman Guide","description":"Software architecture principles","pages":432,"isbn":"9780134494166","publisherId":"00000000-0000-0000-0000-000000000000"}'
	@PutMapping("/{id}")
	public BookDto updateBook(@PathVariable UUID id, @RequestBody BookDto bookDto) {
		return bookService.updateBook(id, bookDto);
	}

	// curl -X DELETE localhost:8080/api/v1/book/{id}
	@DeleteMapping("/{id}")
	public void deleteBook(@PathVariable UUID id) {
		bookService.deleteBook(id);
	}
}

