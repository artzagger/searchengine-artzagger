package searchengine.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteEntity;


@Repository
public interface SiteEntityRepo extends JpaRepository<SiteEntity, Long> {
}
