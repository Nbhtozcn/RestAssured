package Campus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SitemapChecker {
    public static void main(String[] args) {
        try {
            // Fetch and parse the sitemap
            Document doc = Jsoup.connect("https://www.nescafe.com/tr/sitemap.xml").get();
            String sitemapContent = doc.text();

            // Extract URLs from the sitemap using regex
            List<String> urls = extractUrlsFromSitemap(sitemapContent);

            // Check each subpage
            for (String url : urls) {
                boolean isPageError = isPageError(url);
                System.out.println(url + " - " + (isPageError ? "Error" : "OK"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> extractUrlsFromSitemap(String sitemapContent) {
        List<String> urls = new ArrayList<>();
        // Define the regex pattern for URLs in the sitemap
        String regex = "https://www\\.nescafe\\.com/tr/[a-zA-Z0-9\\-\\/]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sitemapContent);
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }

    private static boolean isPageError(String url) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_NOT_FOUND;
        } catch (IOException e) {
            return true; // Treat connection errors as errors
        }
    }
}
