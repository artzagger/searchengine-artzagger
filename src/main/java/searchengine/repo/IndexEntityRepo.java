package searchengine.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexEntity;

import java.util.List;

@Repository
@Transactional
public interface IndexEntityRepo extends JpaRepository<IndexEntity, Long> {
    List<IndexEntity> findByLemmaId(Long lemmaId);
}
