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

import java.util.List;
import java.util.UUID;

import org.example.api.dto.PublisherRequestDto;
import org.example.api.dto.PublisherResponseDto;
import org.example.api.service.PublisherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PublisherController.class)
class PublisherControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PublisherService publisherService;

  @Test
  void getPublisherReturnsPublisher() throws Exception {
    UUID id = UUID.randomUUID();
    PublisherResponseDto publisher = new PublisherResponseDto(id, "Manning", "USA");
    when(publisherService.getPublisher(id)).thenReturn(publisher);

    mockMvc
        .perform(get("/api/v1/publisher/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Manning"))
        .andExpect(jsonPath("$.country").value("USA"));
  }

  @Test
  void getPublishersReturnsPublishers() throws Exception {
    PublisherResponseDto response = new PublisherResponseDto(UUID.randomUUID(), "Packt", "UK");
    when(publisherService.getPublishers()).thenReturn(List.of(response));

    mockMvc
        .perform(get("/api/v1/publisher"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(response.id().toString()))
        .andExpect(jsonPath("$[0].name").value("Packt"))
        .andExpect(jsonPath("$[0].country").value("UK"));
  }

  @Test
  void createPublisherReturnsCreatedPublisher() throws Exception {
    PublisherResponseDto response = new PublisherResponseDto(UUID.randomUUID(), "O'Reilly", "USA");
    String requestJson =
        """
				{"name":"O'Reilly","country":"USA"}
				""";
    when(publisherService.createPublisher(any(PublisherRequestDto.class))).thenReturn(response);

    mockMvc
        .perform(post("/api/v1/publisher").contentType("application/json").content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.id().toString()))
        .andExpect(jsonPath("$.name").value("O'Reilly"))
        .andExpect(jsonPath("$.country").value("USA"));
  }

  @Test
  void updatePublisherReturnsUpdatedPublisher() throws Exception {
    UUID id = UUID.randomUUID();
    PublisherResponseDto response = new PublisherResponseDto(id, "No Starch", "USA");
    String requestJson =
        """
				{"name":"No Starch","country":"USA"}
				""";
    when(publisherService.updatePublisher(eq(id), any(PublisherRequestDto.class))).thenReturn(response);

    mockMvc
        .perform(
            put("/api/v1/publisher/{id}", id).contentType("application/json").content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("No Starch"))
        .andExpect(jsonPath("$.country").value("USA"));
  }

  @Test
  void deletePublisherReturnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    doNothing().when(publisherService).deletePublisher(id);

    mockMvc.perform(delete("/api/v1/publisher/{id}", id)).andExpect(status().isOk());

    verify(publisherService).deletePublisher(id);
  }
}
