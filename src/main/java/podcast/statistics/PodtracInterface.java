package podcast.statistics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author andrewlloyd
 */
public class PodtracInterface {

    private static final Logger LOGGER = Logger.getLogger(PodtracInterface.class.getName());
    private List<String> cookies;
    private HttpURLConnection connection;
    private final String USER_AGENT = "Mozilla/5.0";
    private final PodtracProperties m_properties;
    
    public PodtracInterface(PodtracProperties properties) {
        m_properties = properties;
    }

    
    public int getNinetyDayDownloads() {
        int total = -1;
        try {
            String url = "http://www.podtrac.com/Publisher/account/login";
            String podtrac = "http://www.podtrac.com/Publisher/dashboard/9j2y9ZyeDjT2";
            
            // make sure cookies is turn on
            CookieHandler.setDefault(new CookieManager());
            
            // 1. Send a "GET" request, so that you can extract the form's data.
            String page = GetPageContent(url);
            String postParams = getFormParams(page, m_properties.getUser(), m_properties.getPass());
            
            // 2. Construct above post's content and then send a POST request for
            // authentication
            sendPost(url, postParams);
            
            // 3. success then go to podtrac.
            String result = GetPageContent(podtrac);
            
            
            //testing
//        StringBuilder sb = new StringBuilder();
//        BufferedReader reader = new BufferedReader(new FileReader(new File("/Users/andrewlloyd/Desktop/result.html")));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            sb.append(line);
//        }
//        Document doc = Jsoup.parse(sb.toString());
            Document doc = Jsoup.parse(result);
            Elements tables = doc.select("table");
            boolean found = false;
            for (Element table : tables) {
                Elements rows = table.select("tr");
                for (int i = 0; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    String entry = row.getElementsByClass("stats-cell").select("td").text();
                    if (!entry.isEmpty() && !found) {
                        total = Integer.parseInt(entry);
                        found = true;
                    }
                }
            }
            return total;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return total;
    }
    

    private void sendPost(String url, String postParams) throws Exception {

        URL obj = new URL(url);
        connection = (HttpURLConnection) obj.openConnection();

        // Acts like a browser
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Host", "www.podtrac.com");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (this.cookies != null) {

            for (String cookie : this.cookies) {
                connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Referer", "http://www.podtrac.com/Publisher/account/login");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

        connection.setDoOutput(true);
        connection.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
    }

    private String GetPageContent(String url) throws Exception {

        URL obj = new URL(url);
        connection = (HttpURLConnection) obj.openConnection();

        // default is GET
        connection.setRequestMethod("GET");

        connection.setUseCaches(false);

        // act like a browser
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null) {
            for (String cookie : this.cookies) {
                connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in
                = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies
        setCookies(connection.getHeaderFields().get("Set-Cookie"));

        return response.toString();

    }

    private String getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException {

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        Element loginform = doc.getElementsByTag("form").first();
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("Email")) {
                value = username;
            } else if (key.equals("ClearPasscode")) {
                value = password;
            }
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }
        return result.toString();
    }

    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }

}
