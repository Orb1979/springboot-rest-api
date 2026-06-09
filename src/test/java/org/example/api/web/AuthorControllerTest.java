package org.example.api.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.example.api.dto.AuthorDto;
import org.example.api.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthorService authorService;

  @Test
  void getAuthorReturnsAuthor() throws Exception {
    UUID id = UUID.randomUUID();
    AuthorDto author = new AuthorDto(id, "Jane", "Doe", LocalDate.of(1990, 1, 1));
    when(authorService.getAuthor(id)).thenReturn(author);

    mockMvc
        .perform(get("/api/v1/author/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.firstName").value("Jane"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.birthDate").value("1990-01-01"));
  }

  @Test
  void getAuthorsReturnsAuthors() throws Exception {
    AuthorDto author = new AuthorDto(UUID.randomUUID(), "Jane", "Doe", LocalDate.of(1990, 1, 1));
    when(authorService.getAuthors()).thenReturn(List.of(author));

    mockMvc
        .perform(get("/api/v1/author"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(author.id().toString()))
        .andExpect(jsonPath("$[0].firstName").value("Jane"))
        .andExpect(jsonPath("$[0].lastName").value("Doe"))
        .andExpect(jsonPath("$[0].birthDate").value("1990-01-01"));
  }

  @Test
  void createAuthorReturnsCreatedAuthor() throws Exception {
    AuthorDto created = new AuthorDto(UUID.randomUUID(), "John", "Smith", LocalDate.of(1992, 2, 2));
    String requestJson =
        """
				{"firstName":"John","lastName":"Smith","birthDate":"1992-02-02"}
				""";
    when(authorService.createAuthor(any(AuthorDto.class))).thenReturn(created);

    mockMvc
        .perform(post("/api/v1/author").contentType("application/json").content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(created.id().toString()))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Smith"))
        .andExpect(jsonPath("$.birthDate").value("1992-02-02"));
  }

  @Test
  void updateAuthorReturnsUpdatedAuthor() throws Exception {
    UUID id = UUID.randomUUID();
    AuthorDto updated = new AuthorDto(id, "Janet", "Doe", LocalDate.of(1991, 3, 3));
    String requestJson =
        """
				{"firstName":"Janet","lastName":"Doe","birthDate":"1991-03-03"}
				""";
    when(authorService.updateAuthor(eq(id), any(AuthorDto.class))).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/author/{id}", id).contentType("application/json").content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.firstName").value("Janet"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.birthDate").value("1991-03-03"));
  }

  @Test
  void deleteAuthorReturnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(authorService).deleteAuthor(id);

    mockMvc.perform(delete("/api/v1/author/{id}", id)).andExpect(status().isOk());

    verify(authorService).deleteAuthor(id);
  }
}
