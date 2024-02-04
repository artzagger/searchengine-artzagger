import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.model.PageEntity;

import java.io.IOException;
import java.util.*;

public class DemoLemms {


    public void searchLemmas(String string) throws IOException {

        Map<String, Integer> wordBaseFormListWithCount = new HashMap<>();
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        String regex = "[.,\\s]+";
        String[] parts = string.split(regex);

        for (String s : parts) {

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


        for (Map.Entry<String, Integer> keyList : wordBaseFormListWithCount.entrySet()) {
            System.out.println(keyList.getKey() + " - " + keyList.getValue());
        }
    }

    public boolean isWord(String word) throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        List<String> wordBaseForms = luceneMorph.getMorphInfo(word);
        String part = wordBaseForms.get(0);
        if (part.contains("ПРЕДЛ") || part.contains("СОЮЗ") || part.contains("МС") || part.contains("МЕЖД") || part.contains("ЧАСТ"))
            return false;
        else return true;
    }


    public String clearHTMLTags(PageEntity pageEntity){
        StringBuilder stringBuilder = new StringBuilder();
        Document jsoupDoc = Jsoup.parse(pageEntity.getContent());
        stringBuilder.append(jsoupDoc.body().text());
        return  stringBuilder.toString();
    }

}
