package org.example.api.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.PublisherRequestDto;
import org.example.api.dto.PublisherResponseDto;
import org.example.api.entity.Publisher;
import org.example.api.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PublisherService {

  private final PublisherRepository publisherRepository;

  public PublisherResponseDto getPublisher(UUID id) {
    Publisher publisher = publisherRepository.findById(id).orElse(null);
    return toDto(publisher);
  }

  public List<PublisherResponseDto> getPublishers() {
    return publisherRepository.findAll().stream().map(this::toDto).toList();
  }

  public PublisherResponseDto createPublisher(PublisherRequestDto requestDto) {
    Publisher publisher = toEntity(requestDto);
    Publisher saved = publisherRepository.save(publisher);
    return toDto(saved);
  }

  public PublisherResponseDto updatePublisher(UUID id, PublisherRequestDto requestDto) {
    Publisher existingPublisher =
        publisherRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

    existingPublisher.setName(requestDto.name());
    existingPublisher.setCountry(requestDto.country());

    Publisher saved = publisherRepository.save(existingPublisher);
    return toDto(saved);
  }

  public void deletePublisher(UUID id) {
    if (!publisherRepository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found");
    }
    publisherRepository.deleteById(id);
  }

  private PublisherResponseDto toDto(Publisher publisher) {
    if (publisher == null) {
      return null;
    }
    return new PublisherResponseDto(publisher.getId(), publisher.getName(), publisher.getCountry());
  }

  private Publisher toEntity(PublisherRequestDto dto) {
    if (dto == null) {
      return null;
    }

    Publisher publisher = new Publisher();
    publisher.setName(dto.name());
    publisher.setCountry(dto.country());
    return publisher;
  }
}
