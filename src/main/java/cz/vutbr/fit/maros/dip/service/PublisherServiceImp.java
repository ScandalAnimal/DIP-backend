package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.exception.ResourceNotFoundException;
import cz.vutbr.fit.maros.dip.model.Book;
import cz.vutbr.fit.maros.dip.model.Publisher;
import cz.vutbr.fit.maros.dip.repository.PublisherRepository;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@SuppressWarnings("unchecked")
@Service
@NoArgsConstructor
public class PublisherServiceImp extends PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    public Page<Publisher> getAll(Pageable pageable) {
        return publisherRepository.findAll(pageable);
    }

    @Override
    public Publisher add(Publisher o) {
        return publisherRepository.save(o);
    }

    @Override
    public Publisher update(Publisher o, int id) {
        Publisher publisher = checkIfIdIsPresentandReturnPublisher(id);
        publisher.setName(o.getName());
        publisher.setAddress(o.getAddress());
        return publisherRepository.save(publisher);
    }

    @Override
    public Publisher getById(int id) throws ResourceNotFoundException {
        return checkIfIdIsPresentandReturnPublisher(id);
    }

    @Override
    public Publisher deleteById(int id) {
        Publisher publisher = checkIfIdIsPresentandReturnPublisher(id);
        publisherRepository.deleteById(id);
        return publisher;
    }

    @Override
    public List<Book> getBooksById(int id) {
        return checkIfIdIsPresentandReturnPublisher(id).getBookList();
    }

    private Publisher checkIfIdIsPresentandReturnPublisher(int id) {
        if (!publisherRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException(" Publisher id = " + id + " not found");
        } else {
            return publisherRepository.findById(id).get();
        }
    }
}
