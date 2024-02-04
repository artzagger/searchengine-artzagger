package searchengine.services;

import searchengine.config.Site;
import searchengine.model.*;
import searchengine.repo.IndexEntityRepo;
import searchengine.repo.LemmaEntityRepo;
import searchengine.repo.PageEntityRepo;
import searchengine.repo.SiteEntityRepo;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class PageService implements Runnable {

    private Site site;
    private SiteEntityRepo siteEntityRepo;
    private PageEntityRepo pageEntityRepo;
    private LemmaEntityRepo lemmaEntityRepo;
    private IndexEntityRepo indexEntityRepo;

    public PageService(Site site, SiteEntityRepo siteEntityRepo, PageEntityRepo pageEntityRepo, LemmaEntityRepo lemmaEntityRepo, IndexEntityRepo indexEntityRepo) {
        this.site = site;
        this.siteEntityRepo = siteEntityRepo;
        this.pageEntityRepo = pageEntityRepo;
        this.lemmaEntityRepo = lemmaEntityRepo;
        this.indexEntityRepo = indexEntityRepo;
    }

    @Override
    public void run() {

        SiteEntity siteEntity = new SiteEntity(EnumStatus.INDEXING, LocalDateTime.now(), "", site.getName(), site.getUrl());
        siteEntityRepo.saveAndFlush(siteEntity);

        HashMap<URL, String> pathAndContent = new HashMap<>();
        PageLinkExecutor linkExecutor3 = new PageLinkExecutor(site.getUrl(), pathAndContent);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(linkExecutor3);
        forkJoinPool.shutdownNow();


        for (Map.Entry<URL, String> map : pathAndContent.entrySet()) {
            PageEntity pageEntity = new PageEntity(map.getKey().toString(), 200, map.getValue(), siteEntity);
            pageEntityRepo.saveAndFlush(pageEntity);

            System.out.println("Печатаю номер созданной PageEntity" + pageEntity.getId());

            LemmaService lemmaService = new LemmaService(pageEntity);

            try {
                Map<String, Integer> lemmasOnPage = lemmaService.searchLemmasOnPage();

                for (Map.Entry<String, Integer> lemmasMap : lemmasOnPage.entrySet()) {

                    String lemma = lemmasMap.getKey();
                    List<LemmaEntity> lemmaEntityList = lemmaEntityRepo.findByLemmaContaining(lemma);

                    if (lemmaEntityList.size() > 0) {

                        for (LemmaEntity lemmaEntityIndex : lemmaEntityList) {

                            if (Objects.equals(lemmaEntityIndex.getSiteEntity().getId(), siteEntity.getId())) {
                                LemmaEntity existLemmaEntity = lemmaEntityRepo.findByLemmaContaining(lemma).get(0);
                                int currentFrequency = existLemmaEntity.getFrequency();
                                int newFrequency = currentFrequency + 1;
                                lemmaEntityRepo.setNewFrequency(newFrequency, lemma);

                                IndexEntity indexEntity = new IndexEntity();
                                indexEntity.setLemmaId(existLemmaEntity.getId());
                                indexEntity.setPage_id(pageEntity.getId());
                                indexEntity.setRank(lemmasMap.getValue());
                                indexEntityRepo.saveAndFlush(indexEntity);
                            } else {
                                LemmaEntity lemmaEntity = new LemmaEntity();
                                lemmaEntity.setLemma(lemma);
                                lemmaEntity.setSiteEntity(siteEntity);
                                lemmaEntity.setFrequency(1);
                                lemmaEntityRepo.saveAndFlush(lemmaEntity);

                                IndexEntity indexEntity = new IndexEntity();
                                indexEntity.setLemmaId(lemmaEntity.getId());
                                indexEntity.setPage_id(pageEntity.getId());
                                indexEntity.setRank(lemmasMap.getValue());
                                indexEntityRepo.saveAndFlush(indexEntity);
                            }

                        }

                    } else {

                        LemmaEntity lemmaEntity = new LemmaEntity();
                        lemmaEntity.setLemma(lemma);
                        lemmaEntity.setSiteEntity(siteEntity);
                        lemmaEntity.setFrequency(1);
                        lemmaEntityRepo.saveAndFlush(lemmaEntity);


                        IndexEntity indexEntity = new IndexEntity();
                        indexEntity.setLemmaId(lemmaEntity.getId());
                        indexEntity.setPage_id(pageEntity.getId());
                        indexEntity.setRank(lemmasMap.getValue());
                        indexEntityRepo.saveAndFlush(indexEntity);

                    }


                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
