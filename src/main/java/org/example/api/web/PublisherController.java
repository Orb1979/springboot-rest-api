package org.example.api.web;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.PublisherRequestDto;
import org.example.api.dto.PublisherResponseDto;
import org.example.api.service.PublisherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<PublisherResponseDto> getPublisher(@PathVariable UUID id) {
    return ResponseEntity.ok(publisherService.getPublisher(id));
  }

  @GetMapping
  public  ResponseEntity<List<PublisherResponseDto>> getPublishers() {
    return ResponseEntity.ok(publisherService.getPublishers());
  }

  @PostMapping
  public  ResponseEntity<PublisherResponseDto> createPublisher(@RequestBody PublisherRequestDto requestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(publisherService.createPublisher(requestDto));
  }

  @PutMapping("/{id}")
  public  ResponseEntity<PublisherResponseDto> updatePublisher(
      @PathVariable UUID id, @RequestBody PublisherRequestDto requestDto) {
    return ResponseEntity.ok(publisherService.updatePublisher(id, requestDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePublisher(@PathVariable UUID id) {
    publisherService.deletePublisher(id);
    return ResponseEntity.noContent().build();
  }
}
