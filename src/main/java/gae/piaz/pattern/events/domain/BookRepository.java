package gae.piaz.pattern.events.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Transactional(readOnly = true)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Book findByIdReadOnly(Integer id);
}
