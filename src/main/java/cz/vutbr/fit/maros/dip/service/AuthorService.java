package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.model.Author;
import cz.vutbr.fit.maros.dip.model.Book;

import java.util.List;

public abstract class AuthorService implements MainService<Author> {
    public abstract List<Book> getBooksById(int id);
}
