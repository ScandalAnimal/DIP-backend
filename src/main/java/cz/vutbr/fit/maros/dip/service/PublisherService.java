package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.model.Book;
import cz.vutbr.fit.maros.dip.model.Publisher;

import java.util.List;

/**
 * @author namila
 * @project RESTSpringTest
 * @date 3/26/2019
 * @time 9:59 AM
 */

public abstract class PublisherService implements MainService<Publisher>
{
	public abstract List<Book> getBooksById( int id );
}
