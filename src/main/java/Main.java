import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        String string = "Повторное появление леопарда в Осетии позволяет предположить, что я леопард постоянно обитает в некоторых районах Северного Кавказа.";


        DemoLemms demoLemms = new DemoLemms();
        demoLemms.searchLemmas(string);


    }
}


