package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repo.IndexEntityRepo;
import searchengine.repo.LemmaEntityRepo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class LemmaService {

    private PageEntity pageEntity;

    public LemmaService() {
    }

    public LemmaService(PageEntity pageEntity) {
        this.pageEntity = pageEntity;
    }

    public Map<String, Integer> searchLemmasOnPage() throws IOException {

        String string = clearHTMLTags(pageEntity);
        Map<String, Integer> wordBaseFormListWithCountOnPage = new HashMap<>();

        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        String regex = "[.,\\s]+";
        String regexRUS = "[а-яёА-ЯЁ]+";
        String[] parts = string.split(regex);

        for (String s : parts) {
            if (s.matches(regexRUS)) {
                String part = s.toLowerCase(Locale.ROOT);
                List<String> wordBaseForm = luceneMorph.getNormalForms(part);
                if (isWord(wordBaseForm.get(0))) {
                    String stringKey = wordBaseForm.get(0);
                    if (!wordBaseFormListWithCountOnPage.containsKey(stringKey)) {
                        wordBaseFormListWithCountOnPage.put(stringKey, 1);
                    } else {
                        int value = wordBaseFormListWithCountOnPage.get(stringKey);
                        wordBaseFormListWithCountOnPage.put(stringKey, value + 1);
                    }
                }
            }
        }

        return wordBaseFormListWithCountOnPage;
    }

    public Map<String, Integer> searchLemmasOnSearchPage(String string) throws IOException {

        Map<String, Integer> wordBaseFormListWithCount = new HashMap<>();

        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        String regex = "[.,\\s]+";
        String regexRUS = "[а-яёА-ЯЁ]+";
        String[] parts = string.split(regex);

        for (String s : parts) {
            if (s.matches(regexRUS)) {
                String part = s.toLowerCase(Locale.ROOT);
                List<String> wordBaseForm = luceneMorph.getNormalForms(part);
                if (isWord(wordBaseForm.get(0))) {
                    String stringKey = wordBaseForm.get(0);
                    if (!wordBaseFormListWithCount.containsKey(stringKey)) {
                        wordBaseFormListWithCount.put(stringKey, 1);
                    } else {
                        int value = wordBaseFormListWithCount.get(stringKey);
                        wordBaseFormListWithCount.put(stringKey, value + 1);
                    }
                }
            }
        }

        return wordBaseFormListWithCount;
    }


    public boolean isWord(String word) throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        List<String> wordBaseForms = luceneMorph.getMorphInfo(word);
        String part = wordBaseForms.get(0);

        if (part.contains("ПРЕДЛ") || part.contains("СОЮЗ") || part.contains("МС") || part.contains("МЕЖД") || part.contains("ЧАСТ"))
            return false;
        else return true;
    }


    public String clearHTMLTags(PageEntity pageEntity) {
        StringBuilder stringBuilder = new StringBuilder();
        Document jsoupDoc = Jsoup.parse(pageEntity.getContent());
        stringBuilder.append(jsoupDoc.text());
        return stringBuilder.toString();
    }


}
