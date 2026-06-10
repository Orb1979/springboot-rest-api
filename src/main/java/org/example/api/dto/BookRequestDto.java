package org.example.api.dto;

import java.util.List;
import java.util.UUID;

public record BookRequestDto(
    String title,
    String subTitle,
    String description,
    Integer pages,
    String isbn,
    UUID publisherId,
    List<UUID> authorIds) {}
