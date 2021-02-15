package launchserver.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequestHelper {
    private static HttpURLConnection makeRequest(URL url, String requestMethod) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        return connection;
    }

    public static boolean fileExist(URL url) throws IOException {
        HttpURLConnection request = makeRequest(url, "HEAD");
        int responseCode = request.getResponseCode();
        return responseCode >= 200 && responseCode < 300;
    }

    public static String getFile(URL url) throws IOException {
        HttpURLConnection request = makeRequest(url, "GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
