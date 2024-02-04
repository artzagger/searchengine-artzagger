package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.indexPage.IndexPageResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.startIndexing.StartIndexingResponse;
import searchengine.model.*;
import searchengine.repo.IndexEntityRepo;
import searchengine.repo.LemmaEntityRepo;
import searchengine.repo.PageEntityRepo;
import searchengine.repo.SiteEntityRepo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

    private final SitesList sites;
    private final StopIndexingServiceImpl stopIndexingService;

    @Autowired
    private SiteEntityRepo siteEntityRepo;

    @Autowired
    private PageEntityRepo pageEntityRepo;

    @Autowired
    private LemmaEntityRepo lemmaEntityRepo;

    @Autowired
    private IndexEntityRepo indexEntityRepo;


    @Override
    public StartIndexingResponse addSite() {

        List<Site> sitesList = sites.getSites();
        ExecutorService service = Executors.newFixedThreadPool(1);

        for (Site site : sitesList) {

            System.out.println("new PageService - " + site);
            service.execute(new PageService(site, siteEntityRepo, pageEntityRepo, lemmaEntityRepo, indexEntityRepo));


//            service.execute(() -> {
//                System.out.println(site.getUrl());
//                new PageService(site, siteEntityRepo, pageEntityRepo, lemmaEntityRepo);
//            });
        }

//        final Thread threadIndexing = new Thread(() -> {
//            System.out.println("2" + currentThread().getName() + " - " + currentThread().getState());
//
//            for (Site site : sitesList) {
//                final Thread threadSiteAndPages = new Thread(new PageService(site, siteEntityRepo, pageEntityRepo));
//                threadSiteAndPages.start();
//                try {
//                    threadSiteAndPages.join();
//                } catch (InterruptedException e) {
//                }
//            }

//        });

//        threadIndexing.start();


        //
        StartIndexingResponse response = new StartIndexingResponse();
        response.setResult(true);
        return response;
    }


    @Override
    public IndexPageResponse addPage(String string) {
        System.out.println(string);

        try {
            URI url = new URI(string);
            SiteEntity siteEntity = new SiteEntity(EnumStatus.INDEXED, LocalDateTime.now(), "200", "name", url.getHost());
            siteEntityRepo.saveAndFlush(siteEntity);

            System.out.println(url.toString());

            Document content = Jsoup.connect(url.toString()).userAgent("Mozilla").get();


            PageEntity pageEntity = new PageEntity(url.getPath(), 200, content.toString(), siteEntity);
            pageEntityRepo.saveAndFlush(pageEntity);


            String cleanHTML = clearHTMLTags(pageEntity);

            System.out.println("cleanHTML: " + cleanHTML);


        } catch (MalformedURLException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        //
        IndexPageResponse indexPageResponse = new IndexPageResponse();
        indexPageResponse.setResult(true);
        return indexPageResponse;
    }


    @Override
    public SearchResponse startSearching(String string) throws IOException {

        LemmaService lemmaService = new LemmaService();
        Map<String, Integer> listOfLemmasWithCount = lemmaService.searchLemmasOnSearchPage(string);

        for (Map.Entry<String, Integer> map : listOfLemmasWithCount.entrySet()) {
            List<LemmaEntity> lemmaEntityList = lemmaEntityRepo.findByLemmaContaining(map.getKey());
            int lemmaFrequency = lemmaEntityList.get(0).getFrequency();
            map.setValue(lemmaFrequency);
        }


        Map<String, Integer> sortedListOfLemmasByFrequency = listOfLemmasWithCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));


        //на этом моменте у меня есть список лемм с сортировкой по frequency - sortedListOfLemmasByFrequency

        int count = 0;
        List<IndexEntity> indexEntityList = null;

        for (String key : sortedListOfLemmasByFrequency.keySet()) {
            if (count == 0) {
                List<LemmaEntity> lemmaEntityList = lemmaEntityRepo.findByLemmaContaining(key);
                Long lemmaId = lemmaEntityList.get(0).getId();
                indexEntityList = indexEntityRepo.findByLemmaId(lemmaId);

                System.out.println("Печатаю все pageId первой леммы из списка");
                for (IndexEntity index : indexEntityList) {
                    System.out.println(index.getPage_id());
                }

            } else {
                List<LemmaEntity> lemmaEntityList = lemmaEntityRepo.findByLemmaContaining(key);
                Long lemmaId = lemmaEntityList.get(0).getId();
                if (indexEntityList.containsAll(indexEntityRepo.findByLemmaId(lemmaId))) {
                    System.out.println("Содержит");
                } else System.out.println("Не содержит");

            }
            count++;
        }


        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResult(true);

        return searchResponse;
    }


    public String clearHTMLTags(PageEntity pageEntity) {
        StringBuilder stringBuilder = new StringBuilder();
        Document jsoupDoc = Jsoup.parse(pageEntity.getContent());
        stringBuilder.append(jsoupDoc.body().text());
        return stringBuilder.toString();
    }

    public void print(List<IndexEntity> indexEntityList) {
        for (IndexEntity indexEntity : indexEntityList) {

            System.out.println("LemmaId - " + indexEntity.getLemmaId());


            System.out.println("ID индекса - " + indexEntity.getId() + "; Rank - " + indexEntity.getRank() + " PageId - " + indexEntity.getPage_id());
        }
    }


}


class MyComparator implements Comparator<Map.Entry<String, Integer>> {
    @Override
    public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
        int reverseValueCompared = Integer.compare(e2.getValue(), e1.getValue());
        if (reverseValueCompared == 0) {
            return e1.getKey().compareTo(e2.getKey());
        }
        return reverseValueCompared;
    }
}