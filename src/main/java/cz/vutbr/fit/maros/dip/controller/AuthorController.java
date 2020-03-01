package cz.vutbr.fit.maros.dip.controller;

import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.model.Author;
import cz.vutbr.fit.maros.dip.results.ResponseWrapper;
import cz.vutbr.fit.maros.dip.service.AuthorService;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/author")
public class AuthorController {

    private final AuthorService authorMainService;

    public AuthorController(AuthorService authorMainService) {
        this.authorMainService = authorMainService;
    }

    @GetMapping(value = "/{id}")
    public ResponseWrapper<Author> getAuthorById(
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id) {
        return new ResponseWrapper<>(authorMainService.getById(Integer.parseInt(id)), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseWrapper<Page<Author>> getAuthorAll(Pageable pageable) {
        return new ResponseWrapper<>(authorMainService.getAll(pageable), HttpStatus.OK);
    }

}
