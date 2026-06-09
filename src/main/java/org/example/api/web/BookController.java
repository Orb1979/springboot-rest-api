package org.example.api.web;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.BookRequestDto;
import org.example.api.dto.BookResponseDto;
import org.example.api.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/book")
public class BookController {
  private final BookService bookService;

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
  public ResponseEntity<BookResponseDto> updateBook(
      @PathVariable UUID id, @RequestBody BookRequestDto bookRequestDto) {
    return ResponseEntity.ok(bookService.updateBook(id, bookRequestDto));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<BookResponseDto> patchBook(
      @PathVariable UUID id, @RequestBody BookRequestDto bookRequestDto) {
    return ResponseEntity.ok(bookService.patchBook(id, bookRequestDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }
}
