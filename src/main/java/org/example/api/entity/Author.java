package org.example.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String firstName;

	private String lastName;

	private LocalDate birthDate;

	// inverse side,
	@ManyToMany(mappedBy = "authors")
	private List<Book> books = new ArrayList<>();

}
