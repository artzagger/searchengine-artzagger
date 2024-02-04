package searchengine.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "page", indexes = {
        @Index(
                name = "IDX_PATH",
                columnList = "path"
        )
}
)
@NoArgsConstructor
@Getter
@Setter
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String path;

    private Integer code;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "site_id")
    private SiteEntity siteEntity;

    public PageEntity(String path, Integer code, String content, SiteEntity siteEntity) {
        this.path = path;
        this.code = code;
        this.content = content;
        this.siteEntity = siteEntity;
    }
}
