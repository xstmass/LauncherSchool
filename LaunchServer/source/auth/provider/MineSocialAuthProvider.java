package launchserver.auth.provider;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;
import launcher.helper.IOHelper;
import launcher.helper.LogHelper;
import launcher.serialize.config.entry.BlockConfigEntry;
import launchserver.helpers.HTTPRequestHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

public class MineSocialAuthProvider extends AuthProvider
{
    private static final Pattern UUID_REGEX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    private static final java.net.URL URL;

    static
    {
        try
        {
            URL = new URL("https://authserver.minesocial.net/authenticate");
        }
        catch (MalformedURLException e)
        {
            throw new InternalError(e);
        }
    }

    MineSocialAuthProvider(BlockConfigEntry block)
    {
        super(block);
    }

    @Override
    public AuthProviderResult auth(String login, String password, String ip) throws Throwable
    {
        // https://wiki.vg/Authentication#Payload
        JsonObject request = Json.object().
                add("agent", Json.object().add("name", "Minecraft").add("version", 1)).
                add("username", login).add("password", password);

        // Verify there's no error
        JsonObject response = HTTPRequestHelper.makeAuthlibRequest(URL, request, "MineSocial");
        if (response == null)
        {
            authError("Empty MineSocial Provider response");
        }
        JsonValue errorMessage = response.get("errorMessage");
        if (errorMessage != null)
        {
            authError(errorMessage.asString());
        }

        // Parse JSON data
        JsonObject selectedProfile = response.get("selectedProfile").asObject();
        String username = selectedProfile.get("name").asString();
        String accessToken = response.get("accessToken").asString();
        UUID uuid = UUID.fromString(UUID_REGEX.matcher(selectedProfile.get("id").asString()).replaceFirst("$1-$2-$3-$4-$5"));
        String launcherToken = response.get("clientToken").asString();

        // We're done
        return new MineSocialAuthProviderResult(username, accessToken, uuid, launcherToken);
    }

    @Override
    public void close()
    {
        // Do nothing
    }
}
