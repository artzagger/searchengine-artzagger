package searchengine.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.PageEntity;

@Repository
public interface PageEntityRepo extends JpaRepository<PageEntity, Long> {
}
