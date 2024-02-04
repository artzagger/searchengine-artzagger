package searchengine.services;

import searchengine.dto.indexPage.IndexPageResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.startIndexing.StartIndexingResponse;
import searchengine.model.SiteEntity;

import java.io.IOException;
import java.util.List;

public interface SiteService {
    StartIndexingResponse addSite();

    IndexPageResponse addPage(String url);

    SearchResponse startSearching(String string) throws IOException;
}
