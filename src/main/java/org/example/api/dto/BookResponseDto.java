package org.example.api.dto;

import java.util.List;
import java.util.UUID;

public record BookResponseDto(
    UUID id,
    String title,
    String subTitle,
    String description,
    int pages,
    String isbn,
    PublisherDto publisher,
    List<AuthorDto> authors) {}
