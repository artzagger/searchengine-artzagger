package searchengine.repo;

import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaEntity;

import java.util.List;

@Repository
@Transactional
public interface LemmaEntityRepo extends JpaRepository<LemmaEntity, Long> {

    @Modifying
    @Query(value = "update lemma set frequency = ?1 where lemma = ?2", nativeQuery = true)
    void setNewFrequency(Integer newFrequency, String lemma);

    List<LemmaEntity> findByLemmaContaining(String lemma);
}
