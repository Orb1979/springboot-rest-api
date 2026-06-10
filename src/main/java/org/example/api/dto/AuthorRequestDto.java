package org.example.api.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AuthorRequestDto(String firstName, String lastName, LocalDate birthDate) {}
