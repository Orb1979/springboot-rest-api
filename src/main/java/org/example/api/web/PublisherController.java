package org.example.api.web;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.PublisherDto;
import org.example.api.service.PublisherService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/publisher")
public class PublisherController {

	private final PublisherService publisherService;

	// curl -X GET localhost:8080/api/v1/publisher/{id}
	@GetMapping("/{id}")
	public PublisherDto getPublisher(@PathVariable UUID id) {
		return publisherService.getPublisher(id);
	}

	// curl -X GET localhost:8080/api/v1/publisher
	@GetMapping
	public List<PublisherDto> getPublishers() {
		return publisherService.getPublishers();
	}

	// curl -X POST localhost:8080/api/v1/publisher -H "Content-Type: application/json" -d '{"name":"Penguin","country":"UK"}'
	@PostMapping
	public PublisherDto createPublisher(@RequestBody PublisherDto publisherDto) {
		return publisherService.createPublisher(publisherDto);
	}

	// curl -X PUT localhost:8080/api/v1/publisher/{id} -H "Content-Type: application/json" -d '{"name":"HarperCollins","country":"US"}'
	@PutMapping("/{id}")
	public PublisherDto updatePublisher(@PathVariable UUID id, @RequestBody PublisherDto publisherDto) {
		return publisherService.updatePublisher(id, publisherDto);
	}

	// curl -X DELETE localhost:8080/api/v1/publisher/{id}
	@DeleteMapping("/{id}")
	public void deletePublisher(@PathVariable UUID id) {
		publisherService.deletePublisher(id);
	}
}

