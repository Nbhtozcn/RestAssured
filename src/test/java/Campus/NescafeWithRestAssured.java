package Campus;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NescafeWithRestAssured {
    @Test
    public void subPagesCheckerTest(){
        //this is the link which includes all links on  the web page
        String sitemapUrl = "https://www.nescafe.com/tr/sitemap.xml";

        //get response
        Response response = RestAssured.get(sitemapUrl);


        //convert response to string
        String sitemapContent = response.getBody().asString();


        //extract URLs from the sitemapContent(response) using regex
        List<String> urls = extractUrlsFromSitemapUsingRegex(sitemapContent);

        //check each subpage
        for (String url : urls) {
            boolean isPageError = isPageError(url);
            System.out.println(url + " - " + (isPageError ? "Error" : "OK"));
        }
    }
    private List<String> extractUrlsFromSitemapUsingRegex(String sitemapContent) {
        //this method extract only links from the https://www.nescafe.com/tr/sitemap.xml
        //crate a list
        List<String> urls = new ArrayList<>();

        //define the regex pattern for URLs
        String regex = "https://www\\.nescafe\\.com/tr/[a-zA-Z0-9\\-\\/]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sitemapContent);
        //store extracted links to the list
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }

    private boolean isPageError(String url) {
        //this method checks status code
        Response response = RestAssured.head(url);
        int statusCode = response.getStatusCode();
        return statusCode == 404;
    }
}
