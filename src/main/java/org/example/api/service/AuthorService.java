package org.example.api.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.AuthorRequestDto;
import org.example.api.dto.AuthorResponseDto;
import org.example.api.entity.Author;
import org.example.api.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthorService {
  private final AuthorRepository authorRepository;

  public AuthorResponseDto getAuthor(UUID id) {
    Author author = authorRepository.findById(id).orElse(null);
    return toDto(author);
  }

  public List<AuthorResponseDto> getAuthors() {
    return authorRepository.findAll().stream().map(this::toDto).toList();
  }

  public AuthorResponseDto createAuthor(AuthorRequestDto requestDto) {
    Author author = toEntity(requestDto);
    Author saved = authorRepository.save(author);
    return toDto(saved);
  }

  public AuthorResponseDto updateAuthor(UUID id, AuthorRequestDto requestDto) {
    Author existingAuthor =
        authorRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

    existingAuthor.setFirstName(requestDto.firstName());
    existingAuthor.setLastName(requestDto.lastName());
    existingAuthor.setBirthDate(requestDto.birthDate());

    Author saved = authorRepository.save(existingAuthor);
    return toDto(saved);
  }

  public void deleteAuthor(UUID id) {
    if (!authorRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
    }
    authorRepository.deleteById(id);
  }

  private AuthorResponseDto toDto(Author author) {
    if (author == null) {
      return null;
    }
    return new AuthorResponseDto(
        author.getId(), author.getFirstName(), author.getLastName(), author.getBirthDate());
  }

  private Author toEntity(AuthorRequestDto request) {
    if (request == null) {
      return null;
    }

    Author author = new Author();
    author.setFirstName(request.firstName());
    author.setLastName(request.lastName());
    author.setBirthDate(request.birthDate());
    return author;
  }
}
