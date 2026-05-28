package org.example.api.web;

import org.example.api.dto.BookDto;
import org.example.api.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

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

@WebMvcTest(BookController.class)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;


	@MockitoBean
	private BookService bookService;

	@Test
	void getBookReturnsBook() throws Exception {
		UUID id = UUID.randomUUID();
		UUID publisherId = UUID.randomUUID();
		BookDto book = new BookDto(id, "Clean Code", "A Handbook", "desc", 464, "9780132350884", publisherId);
		when(bookService.getBook(id)).thenReturn(book);

		mockMvc.perform(get("/api/v1/book/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id.toString()))
				.andExpect(jsonPath("$.title").value("Clean Code"))
				.andExpect(jsonPath("$.subTitle").value("A Handbook"))
				.andExpect(jsonPath("$.description").value("desc"))
				.andExpect(jsonPath("$.pages").value(464))
				.andExpect(jsonPath("$.isbn").value("9780132350884"))
				.andExpect(jsonPath("$.publisherId").value(publisherId.toString()));
	}

	@Test
	void getBooksReturnsBooks() throws Exception {
		BookDto book = new BookDto(UUID.randomUUID(), "Book Title", "Sub", "desc", 100, "isbn-1", UUID.randomUUID());
		when(bookService.getBooks()).thenReturn(List.of(book));

		mockMvc.perform(get("/api/v1/book"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(book.id().toString()))
				.andExpect(jsonPath("$[0].title").value("Book Title"))
				.andExpect(jsonPath("$[0].subTitle").value("Sub"))
				.andExpect(jsonPath("$[0].description").value("desc"))
				.andExpect(jsonPath("$[0].pages").value(100))
				.andExpect(jsonPath("$[0].isbn").value("isbn-1"))
				.andExpect(jsonPath("$[0].publisherId").value(book.publisherId().toString()));
	}

	@Test
	void createBookReturnsCreatedBook() throws Exception {
		UUID publisherId = UUID.randomUUID();
		BookDto created = new BookDto(UUID.randomUUID(), "New Book", "Sub", "desc", 123, "isbn-2", publisherId);
		String requestJson = """
				{"title":"New Book","subTitle":"Sub","description":"desc","pages":123,"isbn":"isbn-2","publisherId":"%s"}
				""".formatted(publisherId);
		when(bookService.createBook(any(BookDto.class))).thenReturn(created);

		mockMvc.perform(post("/api/v1/book")
						.contentType("application/json")
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(created.id().toString()))
				.andExpect(jsonPath("$.title").value("New Book"))
				.andExpect(jsonPath("$.subTitle").value("Sub"))
				.andExpect(jsonPath("$.description").value("desc"))
				.andExpect(jsonPath("$.pages").value(123))
				.andExpect(jsonPath("$.isbn").value("isbn-2"))
				.andExpect(jsonPath("$.publisherId").value(publisherId.toString()));
	}

	@Test
	void updateBookReturnsUpdatedBook() throws Exception {
		UUID id = UUID.randomUUID();
		UUID publisherId = UUID.randomUUID();
		BookDto updated = new BookDto(id, "Updated Book", "Updated Sub", "updated desc", 222, "isbn-3", publisherId);
		String requestJson = """
				{"title":"Updated Book","subTitle":"Updated Sub","description":"updated desc","pages":222,"isbn":"isbn-3","publisherId":"%s"}
				""".formatted(publisherId);
		when(bookService.updateBook(eq(id), any(BookDto.class))).thenReturn(updated);

		mockMvc.perform(put("/api/v1/book/{id}", id)
						.contentType("application/json")
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id.toString()))
				.andExpect(jsonPath("$.title").value("Updated Book"))
				.andExpect(jsonPath("$.subTitle").value("Updated Sub"))
				.andExpect(jsonPath("$.description").value("updated desc"))
				.andExpect(jsonPath("$.pages").value(222))
				.andExpect(jsonPath("$.isbn").value("isbn-3"))
				.andExpect(jsonPath("$.publisherId").value(publisherId.toString()));
	}

	@Test
	void deleteBookReturnsOk() throws Exception {
		UUID id = UUID.randomUUID();
		doNothing().when(bookService).deleteBook(id);

		mockMvc.perform(delete("/api/v1/book/{id}", id))
				.andExpect(status().isOk());

		verify(bookService).deleteBook(id);
	}
}
