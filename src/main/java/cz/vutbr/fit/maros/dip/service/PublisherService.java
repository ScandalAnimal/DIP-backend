package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.model.Book;
import cz.vutbr.fit.maros.dip.model.Publisher;

import java.util.List;

public abstract class PublisherService implements MainService<Publisher> {
    public abstract List<Book> getBooksById(int id);
}
