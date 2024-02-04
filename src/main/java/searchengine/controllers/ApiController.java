package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.indexPage.IndexPageResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.startIndexing.StartIndexingResponse;
import searchengine.dto.stopIndexing.StopIndexingResponse;
import searchengine.services.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final SiteService siteService;
    private final StopIndexingService stopIndexingService;
    private final IndexPageService indexPageService;


    public ApiController(StatisticsService statisticsService,
                         SiteService siteService,
                         StopIndexingService stopIndexingService,
                         IndexPageService indexPageService
    ) {
        this.statisticsService = statisticsService;
        this.siteService = siteService;
        this.stopIndexingService = stopIndexingService;
        this.indexPageService = indexPageService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<StartIndexingResponse> startIndexing() {
        return ResponseEntity.ok(siteService.addSite());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<StopIndexingResponse> stopIndexing() {
        return ResponseEntity.ok(stopIndexingService.threadIsStopped());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<IndexPageResponse> indexPage(@RequestParam(name = "url") String url) {
        return ResponseEntity.ok(siteService.addPage(url));
    }


    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(@RequestParam(name = "query") String query) throws IOException {
        return ResponseEntity.ok(siteService.startSearching(query));
    }


}




