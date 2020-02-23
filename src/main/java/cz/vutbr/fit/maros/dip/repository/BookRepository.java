package cz.vutbr.fit.maros.dip.repository;

import cz.vutbr.fit.maros.dip.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>
{
}
