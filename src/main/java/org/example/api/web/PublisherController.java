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

	@GetMapping("/{id}")
	public PublisherDto getPublisher(@PathVariable UUID id) {
		return publisherService.getPublisher(id);
	}

	@GetMapping
	public List<PublisherDto> getPublishers() {
		return publisherService.getPublishers();
	}

	@PostMapping
	public PublisherDto createPublisher(@RequestBody PublisherDto publisherDto) {
		return publisherService.createPublisher(publisherDto);
	}

	@PutMapping("/{id}")
	public PublisherDto updatePublisher(@PathVariable UUID id, @RequestBody PublisherDto publisherDto) {
		return publisherService.updatePublisher(id, publisherDto);
	}

	@DeleteMapping("/{id}")
	public void deletePublisher(@PathVariable UUID id) {
		publisherService.deletePublisher(id);
	}
}

