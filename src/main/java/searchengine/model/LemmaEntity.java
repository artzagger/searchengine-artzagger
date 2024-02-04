package searchengine.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "lemma")
@NoArgsConstructor
@Getter
@Setter
public class LemmaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "site_id")
    private SiteEntity siteEntity;

    @Column(columnDefinition = "VARCHAR(255)")
    private String lemma;

    private int frequency;

}
