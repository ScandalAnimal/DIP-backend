package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.exception.ResourceNotFoundException;
import cz.vutbr.fit.maros.dip.model.Author;
import cz.vutbr.fit.maros.dip.repository.AuthorRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@SuppressWarnings("unchecked")
@Service
@NoArgsConstructor
public class AuthorServiceImp extends AuthorService {

    private AuthorRepository authorRepository;

    @Autowired
    public AuthorServiceImp(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

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

    private Author checkIfIdIsPresentandReturnAuthor(int id) {
        if (!authorRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException(" Author id = " + id + " not found");
        } else {
            return authorRepository.findById(id).get();
        }
    }
}
