package org.example.api.web;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.AuthorDto;
import org.example.api.service.AuthorService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/author")
public class AuthorController {

	private final AuthorService authorService;

	// curl -X GET localhost:8080/api/v1/author/{id}
	@GetMapping("/{id}")
	public AuthorDto getAuthor(@PathVariable UUID id) {
		return authorService.getAuthor(id);
	}

	// curl -X GET localhost:8080/api/v1/author
	@GetMapping()
	public List<AuthorDto> getAuthors() {
		return authorService.getAuthors();
	}

	// curl -X POST localhost:8080/api/v1/author -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","birthDate":"1990-01-01"}'
	@PostMapping
	public AuthorDto createAuthor(@RequestBody AuthorDto authorDto) {
		return authorService.createAuthor(authorDto);
	}

	// curl -X PUT localhost:8080/api/v1/author/{id} -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Doe","birthDate":"1992-02-02"}'
	@PutMapping("/{id}")
	public AuthorDto updateAuthor(@PathVariable UUID id, @RequestBody AuthorDto authorDto) {
		return authorService.updateAuthor(id, authorDto);
	}

	// curl -X DELETE localhost:8080/api/v1/author/{id}
	@DeleteMapping("/{id}")
	public void deleteAuthor(@PathVariable UUID id) {
		authorService.deleteAuthor(id);
	}

}
