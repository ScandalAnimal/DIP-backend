package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.exception.ResourceNotFoundException;
import cz.vutbr.fit.maros.dip.model.Author;
import cz.vutbr.fit.maros.dip.model.Book;
import cz.vutbr.fit.maros.dip.repository.AuthorRepository;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@SuppressWarnings("unchecked")
@Service
@NoArgsConstructor
public class AuthorServiceImp extends AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public Page<Author> getAll(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    @Override
    public Author add(Author o) {
        return authorRepository.save(o);
    }

    @Override
    public Author update(Author o, int id) {
        Author author = checkIfIdIsPresentandReturnAuthor(id);
        author.setName(o.getName());
        author.setAddress(o.getAddress());
        return authorRepository.save(author);
    }

    @Override
    public Author getById(int id) {
        return checkIfIdIsPresentandReturnAuthor(id);
    }

    @Override
    public Author deleteById(int id) {
        Author author = checkIfIdIsPresentandReturnAuthor(id);
        authorRepository.deleteById(id);
        return author;
    }

    @Override
    public List<Book> getBooksById(int id) {
        return checkIfIdIsPresentandReturnAuthor(id).getBookList();
    }

    private Author checkIfIdIsPresentandReturnAuthor(int id) {
        if (!authorRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException(" Author id = " + id + " not found");
        } else {
            return authorRepository.findById(id).get();
        }
    }
}
