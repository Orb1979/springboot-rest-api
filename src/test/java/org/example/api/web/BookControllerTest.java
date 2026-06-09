package org.example.api.web;

import org.example.api.dto.AuthorDto;
import org.example.api.dto.BookRequestDto;
import org.example.api.dto.BookResponseDto;
import org.example.api.dto.PublisherDto;
import org.example.api.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookService bookService;

	private UUID bookId;
	private UUID publisherId;
	private UUID authorId;

	@BeforeEach
	void setUp() {
		bookId = UUID.randomUUID();
		publisherId = UUID.randomUUID();
		authorId = UUID.randomUUID();
	}

	@Test
	void getBookReturnsBook() throws Exception {
		BookResponseDto book = createBookResponseDto(bookId, publisherId, authorId);

		when(bookService.getBook(bookId)).thenReturn(book);

		mockMvc.perform(get("/api/v1/book/{id}", bookId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookId.toString()))
				.andExpect(jsonPath("$.title").value("title"))
				.andExpect(jsonPath("$.subTitle").value("subTitle"))
				.andExpect(jsonPath("$.description").value("description"))
				.andExpect(jsonPath("$.pages").value(100))
				.andExpect(jsonPath("$.isbn").value("isbn-123456"))
				.andExpect(jsonPath("$.publisher.id").value(publisherId.toString()))
				.andExpect(jsonPath("$.publisher.name").value("publisher"))
				.andExpect(jsonPath("$.publisher.country").value("US"))
				.andExpect(jsonPath("$.authors[0].id").value(authorId.toString()))
				.andExpect(jsonPath("$.authors[0].firstName").value("John"))
				.andExpect(jsonPath("$.authors[0].lastName").value("Doe"));
	}

	@Test
	void getBooksReturnsBooks() throws Exception {
		BookResponseDto book = createBookResponseDto(bookId, publisherId, authorId);

		when(bookService.getBooks()).thenReturn(List.of(book));

		mockMvc.perform(get("/api/v1/book"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(bookId.toString()))
				.andExpect(jsonPath("$[0].title").value("title"))
				.andExpect(jsonPath("$[0].publisher.id").value(publisherId.toString()))
				.andExpect(jsonPath("$[0].authors[0].id").value(authorId.toString()));
	}

	@Test
	void createBookReturnsCreatedBook() throws Exception {
		String requestJson = toRequestJson("New Book", "Sub", "desc", 123, "isbn-2");
		BookResponseDto created = createBookResponseDto(
				bookId, "New Book", "Sub", "desc", 123, "isbn-2",
				publisherId, "Publisher", "US", authorId, "John", "Doe", null
		);

		when(bookService.createBook(any(BookRequestDto.class))).thenReturn(created);

		mockMvc.perform(post("/api/v1/book")
				                .contentType("application/json")
				                .content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookId.toString()))
				.andExpect(jsonPath("$.title").value("New Book"))
				.andExpect(jsonPath("$.publisher.id").value(publisherId.toString()))
				.andExpect(jsonPath("$.authors[0].id").value(authorId.toString()));
	}

	@Test
	void updateBookReturnsUpdatedBook() throws Exception {
		String requestJson = toRequestJson("Updated Book", "Sub", "desc", 200, "isbn");
		BookResponseDto updated = createBookResponseDto(
				bookId, "Updated Book", "Sub", "desc", 200, "isbn",
				publisherId, "Publisher", "US", authorId, "Jane", "Smith", null
		);

		when(bookService.updateBook(eq(bookId), any(BookRequestDto.class))).thenReturn(updated);

		mockMvc.perform(put("/api/v1/book/{id}", bookId)
				                .contentType("application/json")
				                .content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookId.toString()))
				.andExpect(jsonPath("$.title").value("Updated Book"))
				.andExpect(jsonPath("$.publisher.id").value(publisherId.toString()))
				.andExpect(jsonPath("$.authors[0].id").value(authorId.toString()));
	}

	@Test
	void patchBookReturnsPatchedBook() throws Exception {
		String requestJson = """
        {
          "title":"Patched Title"
        }
        """;

		BookResponseDto patched = createBookResponseDto(
				bookId, "Patched Title", "subTitle", "description", 100, "isbn-123456",
				publisherId, "publisher", "US", authorId, "John", "Doe", null
		);

		when(bookService.patchBook(eq(bookId), any(BookRequestDto.class))).thenReturn(patched);

		mockMvc.perform(patch("/api/v1/book/{id}", bookId)
				                .contentType("application/json")
				                .content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(bookId.toString()))
				.andExpect(jsonPath("$.title").value("Patched Title"))
				.andExpect(jsonPath("$.subTitle").value("subTitle")) // Stays untouched
				.andExpect(jsonPath("$.publisher.id").value(publisherId.toString()))
				.andExpect(jsonPath("$.authors[0].id").value(authorId.toString()));
	}

	@Test
	void deleteBookReturnsOk() throws Exception {
		doNothing().when(bookService).deleteBook(bookId);

		mockMvc.perform(delete("/api/v1/book/{id}", bookId))
				.andExpect(status().isNoContent());

		verify(bookService).deleteBook(bookId);
	}

	// --- Helper Methods ---

	private String toRequestJson(String title, String subTitle, String description, int pages, String isbn) {
		return """
        {
          "title":"%s",
          "subTitle":"%s",
          "description":"%s",
          "pages":%d,
          "isbn":"%s",
          "publisherId":"%s",
          "authorIds":["%s"]
        }
        """.formatted(title, subTitle, description, pages, isbn, publisherId, authorId);
	}

	private BookResponseDto createBookResponseDto(
			UUID bookId, String title, String subTitle, String description, int pages, String isbn,
			UUID publisherId, String publisherName, String publisherCountry,
			UUID authorId, String authorFirstName, String authorLastName, LocalDate authorBirthDate
	) {
		return new BookResponseDto(
				bookId, title, subTitle, description, pages, isbn,
				new PublisherDto(publisherId, publisherName, publisherCountry),
				List.of(new AuthorDto(authorId, authorFirstName, authorLastName, authorBirthDate))
		);
	}

	private BookResponseDto createBookResponseDto(UUID bookId, UUID publisherId, UUID authorId) {
		return createBookResponseDto(bookId, "title", "subTitle", "description", 100, "isbn-123456",
				publisherId, "publisher", "US", authorId, "John", "Doe", null);
	}
}