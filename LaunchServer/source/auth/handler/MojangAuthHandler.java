package launchserver.auth.handler;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import launcher.serialize.config.entry.BlockConfigEntry;
import launchserver.auth.provider.AuthProviderResult;
import launchserver.auth.provider.MojangAuthProviderResult;
import launchserver.helpers.HTTPRequestHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MojangAuthHandler extends AuthHandler
{
    private static final java.net.URL URL;

    static
    {
        try
        {
            URL = new URL("https://sessionserver.mojang.com/session/minecraft/join");
        }
        catch (MalformedURLException e)
        {
            throw new InternalError(e);
        }
    }

    public final HashMap<String, UUID> usernameToUUID = new HashMap<>();

    MojangAuthHandler(BlockConfigEntry block)
    {
        super(block);
    }

    @Override
    public UUID auth(AuthProviderResult authResult) {
        if (authResult instanceof MojangAuthProviderResult) {
            MojangAuthProviderResult result = (MojangAuthProviderResult) authResult;
            usernameToUUID.put(result.username, result.uuid);
            return result.uuid;
        }
        return null;
    }

    @Override
    public UUID checkServer(String username, String serverID) {
        // .....допустим
        return UUID.fromString(serverID);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean joinServer(String username, String accessToken, String serverID) throws IOException {
        JsonObject request = Json.object().
                add("accessToken", accessToken).add("selectedProfile", usernameToUUID(username).toString().replace("-", "")).
                add("serverId", serverID);

        int response = HTTPRequestHelper.authJoinRequest(URL, request, "Mojang");

        if (200 <= response && response < 300 )
        {
            return true;
        }
        else
        {
            authError("Empty Mojang Handler response");
        }
        return false;
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