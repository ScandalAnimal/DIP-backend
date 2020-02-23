package cz.vutbr.fit.maros.dip.controller;

import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.model.Book;
import cz.vutbr.fit.maros.dip.results.ResponseWrapper;
import cz.vutbr.fit.maros.dip.service.BookService;
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
@RequestMapping("/api/v1/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping(value = "/{id}")
    public ResponseWrapper<Book> getPublisherById(
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id) {
        return new ResponseWrapper<>(bookService.getById(Integer.parseInt(id)), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseWrapper<Page<Book>> getPublisherAll(Pageable pageable) {
        return new ResponseWrapper<>(bookService.getAll(pageable), HttpStatus.OK);
    }

    @PostMapping
    public ResponseWrapper<Book> createPublisher(@Valid @RequestBody Book book) {
        return new ResponseWrapper<>(bookService.add(book), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseWrapper<Book> deletePublisher(
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id) {
        return new ResponseWrapper<>(bookService.deleteById(Integer.parseInt(id)), HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}")
    public ResponseWrapper<Book> updateAuthor(@Valid @RequestBody Book book,
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id) {
        return new ResponseWrapper<>(bookService.update(book, Integer.parseInt(id)), HttpStatus.OK);
    }

}
