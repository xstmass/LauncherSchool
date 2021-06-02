package launchserver.helpers;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;
import launcher.helper.IOHelper;
import launcher.helper.LogHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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

    public static int authJoinRequest(URL url, JsonObject request, String authType) throws IOException
    {
        HttpURLConnection connection = request == null ?
                (HttpURLConnection) IOHelper.newConnection(url) :
                IOHelper.newConnectionPost(url);

        // Make request
        if (request != null)
        {
            connection.setRequestProperty("Content-Type", "application/json");
            try (OutputStream output = connection.getOutputStream())
            {
                output.write(request.toString(WriterConfig.MINIMAL).getBytes(StandardCharsets.UTF_8));
            }
        }
        int statusCode = connection.getResponseCode();
        LogHelper.subDebug("Raw " + authType + " status сode: '" + statusCode + '\'');
        return statusCode;
    }
}
