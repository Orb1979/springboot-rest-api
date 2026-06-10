package org.example.api.web;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.PublisherRequestDto;
import org.example.api.dto.PublisherResponseDto;
import org.example.api.service.PublisherService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/publisher")
public class PublisherController {

  private final PublisherService publisherService;

  @GetMapping("/{id}")
  public PublisherResponseDto getPublisher(@PathVariable UUID id) {
    return publisherService.getPublisher(id);
  }

  @GetMapping
  public List<PublisherResponseDto> getPublishers() {
    return publisherService.getPublishers();
  }

  @PostMapping
  public PublisherResponseDto createPublisher(@RequestBody PublisherRequestDto requestDto) {
    return publisherService.createPublisher(requestDto);
  }

  @PutMapping("/{id}")
  public PublisherResponseDto updatePublisher(
      @PathVariable UUID id, @RequestBody PublisherRequestDto requestDto) {
    return publisherService.updatePublisher(id, requestDto);
  }

  @DeleteMapping("/{id}")
  public void deletePublisher(@PathVariable UUID id) {
    publisherService.deletePublisher(id);
  }
}
