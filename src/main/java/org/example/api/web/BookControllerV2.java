package org.example.api.web;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookRequestDto;
import org.example.api.dto.BookResponseDto;
import org.example.api.service.BookServiceV2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v2/book")
public class BookControllerV2 {
	private final BookServiceV2 bookService;

	@GetMapping("/{id}")
	public ResponseEntity<BookResponseDto> getBook(@PathVariable UUID id) {
		return ResponseEntity.ok(bookService.getBook(id));
	}

	@GetMapping
	public ResponseEntity<List<BookResponseDto>> getBooks() {
		return ResponseEntity.ok(bookService.getBooks());
	}

	@PostMapping
	public ResponseEntity<BookResponseDto> createBook(@RequestBody BookRequestDto bookRequestDto) {
		return ResponseEntity.ok(bookService.createBook(bookRequestDto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<BookResponseDto> updateBook(@PathVariable UUID id, @RequestBody BookRequestDto bookRequestDto) {
		return ResponseEntity.ok(bookService.updateBook(id, bookRequestDto));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<BookResponseDto> patchBook(@PathVariable UUID id, @RequestBody BookRequestDto bookRequestDto) {
		return ResponseEntity.ok(bookService.patchBook(id, bookRequestDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
		bookService.deleteBook(id);
		return ResponseEntity.noContent().build();
	}

	// --- Granular Sub-Resource Endpoints ---

	@PostMapping("/{id}/authors/{authorId}")
	public ResponseEntity<BookResponseDto> addAuthorToBook(
			@PathVariable UUID id,
			@PathVariable UUID authorId) {
		return ResponseEntity.ok(bookService.addAuthorToBook(id, authorId));
	}

	@DeleteMapping("/{id}/authors/{authorId}")
	public ResponseEntity<BookResponseDto> removeAuthorFromBook(
			@PathVariable UUID id,
			@PathVariable UUID authorId) {
		return ResponseEntity.ok(bookService.removeAuthorFromBook(id, authorId));
	}

	@PutMapping("/{id}/authors")
	public ResponseEntity<BookResponseDto> replaceAuthors(
			@PathVariable UUID id,
			@RequestBody List<UUID> authorIds) {
		return ResponseEntity.ok(bookService.replaceAuthors(id, authorIds));
	}

	@DeleteMapping("/{id}/publisher")
	public ResponseEntity<BookResponseDto> removePublisherFromBook(@PathVariable UUID id) {
		return ResponseEntity.ok(bookService.removePublisherFromBook(id));
	}
}