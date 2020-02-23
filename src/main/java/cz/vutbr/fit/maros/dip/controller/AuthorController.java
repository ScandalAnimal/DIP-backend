package cz.vutbr.fit.maros.dip.controller;

import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.model.Author;
import cz.vutbr.fit.maros.dip.model.Book;
import cz.vutbr.fit.maros.dip.results.ResponseWrapper;
import cz.vutbr.fit.maros.dip.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/author")
public class AuthorController
{

	@Autowired
	private AuthorService authorMainService;

	@GetMapping(value = "/{id}")
	public ResponseWrapper<Author> getAuthorById(
			@Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id") String id )
	{
		return new ResponseWrapper<>( authorMainService.getById( Integer.parseInt( id ) ), HttpStatus.OK );
	}

	@GetMapping()
	public ResponseWrapper<Page<Author>> getAuthorAll( Pageable pageable )
	{
		return new ResponseWrapper<>( authorMainService.getAll( pageable ), HttpStatus.OK );
	}

	@PostMapping
	public ResponseWrapper<Author> createAuthor( @Valid @RequestBody Author author )
	{
		return new ResponseWrapper<>( authorMainService.add( author ), HttpStatus.OK );
	}

	@DeleteMapping(value = "/{id}")
	public ResponseWrapper<Author> deleteAuthor(
			@Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id") String id )
	{
		return new ResponseWrapper<>( authorMainService.deleteById( Integer.parseInt( id ) ), HttpStatus.OK );
	}

	@PatchMapping(value = "/{id}")
	public ResponseWrapper<Author> UpdateAuthor( @Valid @RequestBody Author author,
			@Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id") String id )
	{
		return new ResponseWrapper<>( authorMainService.update( author, Integer.parseInt( id ) ), HttpStatus.OK );
	}

	@GetMapping(value = "/{id}/books")
	public ResponseWrapper<List<Book>> getAuthorBooksById(
			@Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id") String id )
	{
		return new ResponseWrapper<>( authorMainService.getBooksById( Integer.parseInt( id ) ), HttpStatus.OK );
	}

}
