package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.indexPage.IndexPageResponse;
import searchengine.model.EnumStatus;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repo.PageEntityRepo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IndexPageServiceImpl implements IndexPageService {

    @Autowired
    private PageEntityRepo pageEntityRepo;

    @Override
    public IndexPageResponse addPage(String string) {
        System.out.println(string);
        IndexPageResponse indexPageResponse = new IndexPageResponse();
        indexPageResponse.setResult(true);
        return indexPageResponse;
    }

    public String clearHTMLTags(PageEntity pageEntity) {
        StringBuilder stringBuilder = new StringBuilder();
        Document jsoupDoc = Jsoup.parse(pageEntity.getContent());
        stringBuilder.append(jsoupDoc.body().text());
        return stringBuilder.toString();
    }
}
