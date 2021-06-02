package launchserver.auth.handler;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;
import launcher.helper.IOHelper;
import launcher.helper.LogHelper;
import launcher.serialize.config.entry.BlockConfigEntry;
import launchserver.auth.provider.AuthProviderResult;
import launchserver.auth.provider.MineSocialAuthProviderResult;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineSocialAuthHandler extends AuthHandler
{
    private static final java.net.URL URL;

    static
    {
        try
        {
            URL = new URL("https://sessionserver.minesocial.net/session/minecraft/join");
        }
        catch (MalformedURLException e)
        {
            throw new InternalError(e);
        }
    }

    public final HashMap<String, UUID> usernameToUUID = new HashMap<>();

    MineSocialAuthHandler(BlockConfigEntry block)
    {
        super(block);
    }

    @Override
    public UUID auth(AuthProviderResult authResult) {
        if (authResult instanceof MineSocialAuthProviderResult) {
            MineSocialAuthProviderResult result = (MineSocialAuthProviderResult) authResult;
            usernameToUUID.put(result.username, result.uuid);
            return result.uuid;
        }
        return null;
    }

    @Override
    public UUID checkServer(String username, String serverID) {
        return UUID.fromString(serverID);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean joinServer(String username, String accessToken, String serverID) throws IOException {
        JsonObject request = Json.object().
                add("agent", Json.object().add("name", "Minecraft").add("version", 1)).
                add("accessToken", accessToken).add("selectedProfile", usernameToUUID(username).toString().replace("-", "")).
                add("serverId", serverID);

        int response = makeMineSocialRequest(URL, request);

        if (200 <= response && response < 300 )
        {
            return true;
        }
        else
        {
            authError("Empty MineSocial Handler response");
        }
        return false;
    }

    public static int makeMineSocialRequest(URL url, JsonObject request) throws IOException
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
        LogHelper.subDebug("Raw MineSocial status сode: '" + statusCode + '\'');
        return statusCode;
    }

    @Override
    public UUID usernameToUUID(String username) {
        return usernameToUUID.get(username);
    }

    @Override
    public String uuidToUsername(UUID uuid) {
        for (Map.Entry<String, UUID> entry : usernameToUUID.entrySet()) {
            if (entry.getValue().equals(uuid)) return entry.getKey();
        }
        return null;
    }
}
