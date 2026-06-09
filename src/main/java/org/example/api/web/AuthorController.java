package org.example.api.web;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.AuthorDto;
import org.example.api.service.AuthorService;
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
  public AuthorDto getAuthor(@PathVariable UUID id) {
    return authorService.getAuthor(id);
  }

  @GetMapping()
  public List<AuthorDto> getAuthors() {
    return authorService.getAuthors();
  }

  @PostMapping
  public AuthorDto createAuthor(@RequestBody AuthorDto authorDto) {
    return authorService.createAuthor(authorDto);
  }

  @PutMapping("/{id}")
  public AuthorDto updateAuthor(@PathVariable UUID id, @RequestBody AuthorDto authorDto) {
    return authorService.updateAuthor(id, authorDto);
  }

  @DeleteMapping("/{id}")
  public void deleteAuthor(@PathVariable UUID id) {
    authorService.deleteAuthor(id);
  }
}
