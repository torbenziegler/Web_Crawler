package hyperskill;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLParser {

    private final URL pageUrl;
    private String pageContent;

    public HTMLParser(URL pageUrl) {
        this.pageUrl = pageUrl;
    }

    public void loadContent() throws IOException {
        URLConnection urlConnection = new URL(pageUrl.toString()).openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");

        if (urlConnection.getContentType() == null || !urlConnection.getContentType().contains("text/html")) {
            throw new MalformedURLException(pageUrl.toString() + " content type != text/html");
        }

        try (InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream())) {
            pageContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public String getTitle() {
        Pattern pattern = Pattern.compile("(?<=<title>)(.*)(?=</title>)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pageContent);

        return matcher.find() ? matcher.group() : "";
    }

    public Map<String, String> findLinks() {
        HashMap<String, String> links =  new HashMap<>();

        Pattern linkPattern = Pattern.compile("<a.*href=(\".+\"|'.+').*>\\w+</a>");
        Matcher linkMatcher = linkPattern.matcher(pageContent);

        Pattern urlPattern = Pattern.compile("((?<=href=\")|(?<=href='))[\\w/:.\\-#]+");

        while (linkMatcher.find()) {
            String link = linkMatcher.group();
            Matcher urlMatcher = urlPattern.matcher(link);

            if (urlMatcher.find()) {
                try {
                    URL linkUrl = new URL(pageUrl, urlMatcher.group());

                    HTMLParser linkParser = new HTMLParser(linkUrl);
                    linkParser.loadContent();

                    links.put(
                            linkUrl.toString(),
                            linkParser.getTitle()
                    );

                } catch (MalformedURLException exception) {
                    System.out.println(exception);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return links;
    }
}
