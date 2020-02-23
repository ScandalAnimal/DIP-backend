package cz.vutbr.fit.maros.dip.repository;

import cz.vutbr.fit.maros.dip.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer>
{

}
