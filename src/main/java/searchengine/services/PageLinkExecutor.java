package searchengine.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class PageLinkExecutor extends RecursiveTask<HashMap<URL, String>> {

    private String url;
    private HashMap<URL, String> pathAndContent;
    private List<String> urlList = new ArrayList<>();
    List<PageLinkExecutor> taskList = new ArrayList<>();
    private static int count = 0;

    public PageLinkExecutor(String url, HashMap<URL, String> pathAndContent) {
        this.url = url;
        this.pathAndContent = pathAndContent;
    }

    @Override
    protected HashMap<URL, String> compute() {
        try {
            Thread.sleep(250);
            Document content = Jsoup.connect(url).userAgent("Mozilla").get();

            Elements elements = content.select("a");
            elements.forEach(element -> {
                String currentUrl = element.attr("abs:href");
                if (checkUrl(currentUrl, url) &&
                        !isFile(currentUrl) &&
                        !currentUrl.equals(url) &&
                        !urlList.contains(currentUrl) && (pathAndContent.size() < 5)) {
                    urlList.add(currentUrl);
                    try {
                        URL url = new URL(currentUrl);
                        if (!pathAndContent.containsKey(url)) {

                            pathAndContent.putIfAbsent(url, content.text());
                        }
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Это в linkExecutor: " + count + " - " + currentUrl);

                    PageLinkExecutor linkExecutor = new PageLinkExecutor(currentUrl, pathAndContent);
                    linkExecutor.fork();
                    taskList.add(linkExecutor);
                    count++;
                }
            });
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println(e);
            e.printStackTrace();
        }
        for (PageLinkExecutor link : taskList) {
            link.join();
        }
        return pathAndContent;
    }

    public static boolean checkUrl(String currentUrl, String url) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return currentUrl != null && currentUrl.matches(regex) && currentUrl.contains(url) && !currentUrl.endsWith("kids/") && !currentUrl.endsWith("#");
    }

    public static boolean isFile(String url) {
        url.toLowerCase();
        return url.contains(".jpg")
                || url.contains(".jpeg")
                || url.contains(".png")
                || url.contains(".gif")
                || url.contains(".webp")
                || url.contains(".ico")
                || url.contains(".pdf")
                || url.contains(".eps")
                || url.contains(".xlsx")
                || url.contains(".doc")
                || url.contains(".pptx")
                || url.contains(".docx")
                || url.contains("?_ga");
    }
}
