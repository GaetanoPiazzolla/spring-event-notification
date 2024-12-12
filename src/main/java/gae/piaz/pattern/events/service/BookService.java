package gae.piaz.pattern.events.service;

import gae.piaz.pattern.events.domain.Book;
import gae.piaz.pattern.events.domain.BookRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Integer id) {
        return bookRepository.findById(id);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Integer id, String title, String description) {
        Book book = bookRepository.findByIdReadOnly(id);
        book.setTitle(title);
        book.setDescription(description);
        return bookRepository.saveAndFlush(book);
    }

    public void deleteById(Integer id) {
        bookRepository.deleteById(id);
    }
}
