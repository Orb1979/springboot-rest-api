package org.example.api.repository;

import org.example.api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

	@Query("""
    SELECT b
    FROM Book b
    LEFT JOIN FETCH b.authors
    LEFT JOIN FETCH b.publisher
    WHERE b.id = :id
""")
	Optional<Book> findByIdHydrated(UUID id);


	@Query("""
    SELECT b
    FROM Book b
    LEFT JOIN FETCH b.authors
    LEFT JOIN FETCH b.publisher
""")
	Optional<Book> findAllHydrated();

}
