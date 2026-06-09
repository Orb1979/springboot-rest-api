package org.example.api;


import org.example.api.entity.Author;
import org.example.api.entity.Book;
import org.example.api.entity.Publisher;
import org.example.api.repository.AuthorRepository;
import org.example.api.repository.BookRepository;
import org.example.api.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
Integration test, which adds data to our local db
through controller endpoints create a publisher, author and a book with publisher and author
*/

@SpringBootTest()
@AutoConfigureMockMvc
@TestPropertySource(value = "classpath:integration.properties")
@ActiveProfiles("integration")
public class IntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private PublisherRepository publisherRepository;

	@BeforeEach
	void cleanDatabase() {
		bookRepository.deleteAll();
		authorRepository.deleteAll();
		publisherRepository.deleteAll();
	}

	@Test
	void apiComponentsWorkTogetherAndPersistToPostgres() throws Exception {
		String createdPublisherJson = mockMvc.perform(post("/api/v1/publisher")
						.contentType("application/json")
						.content("""
								{"name":"Manning","country":"USA"}
								"""))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		UUID publisherId = UUID.fromString(extractJsonValue(createdPublisherJson, "id"));

		String createdAuthorJson = mockMvc.perform(post("/api/v1/author")
						.contentType("application/json")
						.content("""
								{"firstName":"Robert","lastName":"Martin","birthDate":"1952-12-05"}
								"""))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		UUID authorId = UUID.fromString(extractJsonValue(createdAuthorJson, "id"));

		String createdBookJson = mockMvc.perform(post("/api/v1/book")
						.contentType("application/json")
						.content("""
								{
								"title":"Clean Architecture",
								"subTitle":"A Craftsman's Guide to Software Structure and Design",
								"description":"Architecture principles and practices",
								"pages":432,
								"isbn":"9780134494166",
								"publisherId":"%s",
								"authorIds":["%s"]
								}
								""".formatted(publisherId, authorId)))
				.andReturn()
				.getResponse()
				.getContentAsString();
		UUID bookId = UUID.fromString(extractJsonValue(createdBookJson, "id"));

		mockMvc.perform(get("/api/v1/publisher/{id}", publisherId))
				.andExpect(status().isOk());
		mockMvc.perform(get("/api/v1/author/{id}", authorId))
				.andExpect(status().isOk());
		mockMvc.perform(get("/api/v1/book/{id}", bookId))
				.andExpect(status().isOk());

		assertEquals(1, publisherRepository.count());
		assertEquals(1, authorRepository.count());
		assertEquals(1, bookRepository.count());

		Publisher savedPublisher = publisherRepository.findById(publisherId).orElseThrow();
		assertEquals("Manning", savedPublisher.getName());
		assertEquals("USA", savedPublisher.getCountry());

		Author savedAuthor = authorRepository.findById(authorId).orElseThrow();
		assertEquals("Robert", savedAuthor.getFirstName());
		assertEquals("Martin", savedAuthor.getLastName());

		Book savedBook = bookRepository.findById(bookId).orElseThrow();
		assertEquals("Clean Architecture", savedBook.getTitle());
		assertNotNull(savedBook.getPublisher());
		assertEquals(publisherId, savedBook.getPublisher().getId());
	}

	private String extractJsonValue(String json, String fieldName) {
		String marker = "\"" + fieldName + "\":\"";
		int start = json.indexOf(marker);
		int valueStart = start + marker.length();
		int valueEnd = json.indexOf("\"", valueStart);
		return json.substring(valueStart, valueEnd);
	}
}
