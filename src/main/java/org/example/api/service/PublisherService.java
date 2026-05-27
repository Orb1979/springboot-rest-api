package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.PublisherDto;
import org.example.api.entity.Publisher;
import org.example.api.repository.PublisherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublisherService {

	private final PublisherRepository publisherRepository;

	public PublisherDto getPublisher(UUID id) {
		Publisher publisher = publisherRepository.findById(id).orElse(null);
		return toDto(publisher);
	}

	public List<PublisherDto> getPublishers() {
		return publisherRepository.findAll().stream().map(this::toDto).toList();
	}

	public PublisherDto createPublisher(PublisherDto publisherDto) {
		Publisher publisher = toEntity(publisherDto);
		Publisher saved = publisherRepository.save(publisher);
		return toDto(saved);
	}

	public PublisherDto updatePublisher(UUID id, PublisherDto publisherDto) {
		Publisher existingPublisher = publisherRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found"));

		existingPublisher.setName(publisherDto.name());
		existingPublisher.setCountry(publisherDto.country());

		Publisher saved = publisherRepository.save(existingPublisher);
		return toDto(saved);
	}

	public void deletePublisher(UUID id) {
		if (!publisherRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Publisher not found");
		}
		publisherRepository.deleteById(id);
	}

	private PublisherDto toDto(Publisher publisher) {
		if (publisher == null) {
			return null;
		}
		return new PublisherDto(
				publisher.getId(),
				publisher.getName(),
				publisher.getCountry()
		);
	}

	private Publisher toEntity(PublisherDto dto) {
		if (dto == null) {
			return null;
		}

		Publisher publisher = new Publisher();
		if (dto.id() != null) {
			publisher.setId(dto.id());
		}
		publisher.setName(dto.name());
		publisher.setCountry(dto.country());
		return publisher;
	}
}

