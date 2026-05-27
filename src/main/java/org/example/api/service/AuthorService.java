package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.AuthorDto;
import org.example.api.entity.Author;
import org.example.api.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {
	private final AuthorRepository authorRepository;

	public AuthorDto getAuthor(UUID id) {
		Author author = authorRepository.findById(id).orElse(null);
		return toDto(author);
	}

	public List<AuthorDto> getAuthors() {
		return authorRepository.findAll().stream().map(this::toDto).toList();
	}

	public AuthorDto createAuthor(AuthorDto authorDto) {
		Author author = toEntity(authorDto);
		Author saved = authorRepository.save(author);
		return toDto(saved);
	}

	public AuthorDto updateAuthor(UUID id, AuthorDto authorDto) {
		Author existingAuthor = authorRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

		existingAuthor.setFirstName(authorDto.firstName());
		existingAuthor.setLastName(authorDto.lastName());
		existingAuthor.setBirthDate(authorDto.birthDate());

		Author saved = authorRepository.save(existingAuthor);
		return toDto(saved);
	}

	public void deleteAuthor(UUID id) {
		if (!authorRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
		}
		authorRepository.deleteById(id);
	}

	private AuthorDto toDto(Author author) {
		if (author == null) {
			return null;
		}
		return new AuthorDto(
				author.getId(),
				author.getFirstName(),
				author.getLastName(),
				author.getBirthDate()
		);
	}

	private Author toEntity(AuthorDto dto) {
		if (dto == null) {
			return null;
		}

		Author author = new Author();
		if (dto.id() != null) {
			author.setId(dto.id());
		}
		author.setFirstName(dto.firstName());
		author.setLastName(dto.lastName());
		author.setBirthDate(dto.birthDate());
		return author;
	}
}
