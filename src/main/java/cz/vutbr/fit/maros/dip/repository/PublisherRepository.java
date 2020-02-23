package cz.vutbr.fit.maros.dip.repository;

import cz.vutbr.fit.maros.dip.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer>
{
}
