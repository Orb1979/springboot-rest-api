package org.example.api.dto;

import java.util.UUID;

public record BookDto(
		UUID id,
		String title,
		String subTitle,
		String description,
		int pages,
		String isbn,
		UUID publisherId
) {
}

