package org.example.api.service;

import org.example.api.dto.PublisherDto;
import org.example.api.entity.Publisher;
import org.example.api.repository.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublisherServiceTest {

	@Mock
	private PublisherRepository publisherRepository;

	@InjectMocks
	private PublisherService publisherService;

	@Test
	void createPublisherSavesAndReturnsPublisher() {
		PublisherDto publisherDto = new PublisherDto(null, "publisherName", "country");
		Publisher publisher = new Publisher();
		publisher.setName("publisherName");
		publisher.setCountry("country");
		when(publisherRepository.save(any(Publisher.class))).thenReturn(publisher);

		PublisherDto result = publisherService.createPublisher(publisherDto);

		assertEquals("publisherName", result.name());
		assertEquals("country", result.country());
		verify(publisherRepository).save(any(Publisher.class));
	}

	@Test
	void updatePublisherUpdatesAndSavesExistingPublisher() {
		UUID id = UUID.randomUUID();
		Publisher existingPublisher = new Publisher();
		existingPublisher.setName("Old Name");
		existingPublisher.setCountry("Old Country");

		PublisherDto updatedPublisher = new PublisherDto(null, "New Name", "New Country");

		when(publisherRepository.findById(id)).thenReturn(Optional.of(existingPublisher));
		when(publisherRepository.save(existingPublisher)).thenReturn(existingPublisher);

		PublisherDto result = publisherService.updatePublisher(id, updatedPublisher);

		assertEquals("New Name", result.name());
		assertEquals("New Country", result.country());
		verify(publisherRepository).save(existingPublisher);
	}

	@Test
	void updatePublisherThrowsWhenPublisherNotFound() {
		UUID id = UUID.randomUUID();
		when(publisherRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(ResponseStatusException.class, () -> publisherService.updatePublisher(id, new PublisherDto(null, "a", "b")));
		verify(publisherRepository, never()).save(any());
	}

	@Test
	void deletePublisherDeletesWhenFound() {
		UUID id = UUID.randomUUID();
		when(publisherRepository.existsById(id)).thenReturn(true);

		publisherService.deletePublisher(id);

		verify(publisherRepository).deleteById(id);
	}

	@Test
	void deletePublisherThrowsWhenNotFound() {
		UUID id = UUID.randomUUID();
		when(publisherRepository.existsById(id)).thenReturn(false);

		assertThrows(ResponseStatusException.class, () -> publisherService.deletePublisher(id));
		verify(publisherRepository, never()).deleteById(any());
	}
}