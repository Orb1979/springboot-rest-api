package org.example.api.web;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.AuthorRequestDto;
import org.example.api.dto.AuthorResponseDto;
import org.example.api.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/author")
public class AuthorController {
  private final AuthorService authorService;

  @GetMapping("/{id}")
  public ResponseEntity<AuthorResponseDto> getAuthor(@PathVariable UUID id) {
    return ResponseEntity.ok(authorService.getAuthor(id));
  }

  @GetMapping()
  public ResponseEntity<List<AuthorResponseDto>> getAuthors() {
    return ResponseEntity.ok(authorService.getAuthors());
  }

  @PostMapping
  public ResponseEntity<AuthorResponseDto> createAuthor(@RequestBody AuthorRequestDto requestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(requestDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AuthorResponseDto> updateAuthor(
      @PathVariable UUID id, @RequestBody AuthorRequestDto requestDto) {
    return ResponseEntity.ok(authorService.updateAuthor(id, requestDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAuthor(@PathVariable UUID id) {
    authorService.deleteAuthor(id);
    return ResponseEntity.noContent().build();
  }
}
