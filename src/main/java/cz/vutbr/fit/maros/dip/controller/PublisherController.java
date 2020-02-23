package cz.vutbr.fit.maros.dip.controller;

import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.exception.ResourceNotFoundException;
import cz.vutbr.fit.maros.dip.model.Book;
import cz.vutbr.fit.maros.dip.model.Publisher;
import cz.vutbr.fit.maros.dip.results.ResponseWrapper;
import cz.vutbr.fit.maros.dip.service.PublisherService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/publisher")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping(value = "/{id}")
    public ResponseWrapper<Publisher> getPublisherById(
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id)
            throws ResourceNotFoundException {
        return new ResponseWrapper<>(publisherService.getById(Integer.parseInt(id)), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseWrapper<Page<Publisher>> getPublisherAll(Pageable pageable) {
        return new ResponseWrapper<>(publisherService.getAll(pageable), HttpStatus.OK);
    }

    @PostMapping
    public ResponseWrapper<Publisher> createPublisher(@Valid @RequestBody Publisher publisher)
            throws ResourceNotFoundException {
        return new ResponseWrapper<>(publisherService.add(publisher), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseWrapper<Publisher> deletePublisher(
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id)
            throws ResourceNotFoundException {
        return new ResponseWrapper<>(publisherService.deleteById(Integer.parseInt(id)), HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}")
    public ResponseWrapper<Publisher> updateAuthor(@Valid @RequestBody Publisher publisher,
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id)
            throws ResourceNotFoundException {
        return new ResponseWrapper<>(publisherService.update(publisher, Integer.parseInt(id)), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/books")
    public ResponseWrapper<List<Book>> getPublisherBooksById(
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id)
            throws ResourceNotFoundException {
        return new ResponseWrapper<>(publisherService.getBooksById(Integer.parseInt(id)), HttpStatus.OK);
    }

}
