package org.example.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.example.api.dto.AuthorRequestDto;
import org.example.api.dto.AuthorResponseDto;
import org.example.api.entity.Author;
import org.example.api.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  @Mock private AuthorRepository authorRepository;

  @InjectMocks private AuthorService authorService;

  @Test
  void createAuthorSavesAndReturnsAuthor() {
    AuthorRequestDto requestDto = new AuthorRequestDto("John", "Doe", java.time.LocalDate.of(1990, 1, 1));
    Author author = new Author();
    author.setFirstName("John");
    author.setLastName("Doe");
    author.setBirthDate(java.time.LocalDate.of(1990, 1, 1));
    when(authorRepository.save(any(Author.class))).thenReturn(author);

    AuthorResponseDto result = authorService.createAuthor(requestDto);

    assertEquals("John", result.firstName());
    assertEquals("Doe", result.lastName());
    assertEquals(java.time.LocalDate.of(1990, 1, 1), result.birthDate());
    verify(authorRepository).save(any(Author.class));
  }

  @Test
  void updateAuthorUpdatesAndSavesExistingAuthor() {
    UUID id = UUID.randomUUID();
    Author existingAuthor = new Author();
    existingAuthor.setFirstName("Old");
    existingAuthor.setLastName("Name");
    existingAuthor.setBirthDate(LocalDate.of(1990, 1, 1));

    AuthorRequestDto requestDto = new AuthorRequestDto("New", "Author", LocalDate.of(1995, 5, 10));

    when(authorRepository.findById(id)).thenReturn(Optional.of(existingAuthor));
    when(authorRepository.save(existingAuthor)).thenReturn(existingAuthor);

    AuthorResponseDto result = authorService.updateAuthor(id, requestDto);

    assertEquals("New", result.firstName());
    assertEquals("Author", result.lastName());
    assertEquals(LocalDate.of(1995, 5, 10), result.birthDate());
    verify(authorRepository).save(existingAuthor);
  }

  @Test
  void updateAuthorThrowsWhenAuthorNotFound() {
    UUID id = UUID.randomUUID();
    when(authorRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(
        ResponseStatusException.class,
        () -> authorService.updateAuthor(id, new AuthorRequestDto("a", "b", null)));
    verify(authorRepository, never()).save(any());
  }

  @Test
  void deleteAuthorDeletesWhenFound() {
    UUID id = UUID.randomUUID();
    when(authorRepository.existsById(id)).thenReturn(true);

    authorService.deleteAuthor(id);

    verify(authorRepository).deleteById(id);
  }

  @Test
  void deleteAuthorThrowsWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(authorRepository.existsById(id)).thenReturn(false);

    assertThrows(ResponseStatusException.class, () -> authorService.deleteAuthor(id));
    verify(authorRepository, never()).deleteById(any());
  }
}
