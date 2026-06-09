package org.example.api.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

  @Query(
      """
    SELECT b
    FROM Book b
    LEFT JOIN FETCH b.authorBooks ab
    LEFT JOIN FETCH ab.author
    LEFT JOIN FETCH b.publisher
    WHERE b.id = :id
""")
  Optional<Book> findByIdHydrated(UUID id);

  @Query(
      """
    SELECT b
    FROM Book b
    LEFT JOIN FETCH b.authorBooks ab
    LEFT JOIN FETCH ab.author
    LEFT JOIN FETCH b.publisher
""")
  Optional<Book> findAllHydrated();
}
