package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "site")
@NoArgsConstructor
@Getter
@Setter
public class SiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private EnumStatus status;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime status_time;

    @Column(columnDefinition = "TEXT")
    private String last_error;

    @Column(columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(columnDefinition = "VARCHAR(255)")
    private String url;

    @OneToMany(targetEntity = searchengine.model.PageEntity.class, mappedBy = "siteEntity", fetch = FetchType.EAGER)
    private Set<searchengine.model.PageEntity> pageEntities = new HashSet<>();

    public SiteEntity(EnumStatus status, LocalDateTime status_time, String last_error, String name, String url) {
        this.status = status;
        this.status_time = status_time;
        this.last_error = last_error;
        this.name = name;
        this.url = url;
    }
}
