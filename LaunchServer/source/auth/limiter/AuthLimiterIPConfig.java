package launchserver.auth.limiter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import launcher.helper.IOHelper;
import launcher.helper.LogHelper;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AuthLimiterIPConfig
{
    public static Path ipConfigFile;
    public static Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    public static AuthLimiterIPConfig Instance; // С этим сами разбирайтесь)

    @Expose
    List<String> allowIp = new ArrayList<>();
    @Expose
    List<String> blockIp = new ArrayList<>();

    public static void load(Path file) throws Exception {
        ipConfigFile = file;
        if (IOHelper.exists(ipConfigFile)) {
            LogHelper.subDebug("IP List file found! Loading...");
            try
            {
                Instance = gson.fromJson(IOHelper.newReader(ipConfigFile), AuthLimiterIPConfig.class);
                return;
            }
            catch (JsonIOException | IOException error) {
                LogHelper.subWarning("Ip List not reading!");
                if (LogHelper.isDebugEnabled()) LogHelper.error(error);
            }
            catch (JsonSyntaxException error) {
                LogHelper.subWarning("Invalid file syntax!");
                if (LogHelper.isDebugEnabled()) LogHelper.error(error);
            }
        }

        LogHelper.subWarning("IP List file not found! Creating file...");
        Instance = new AuthLimiterIPConfig();
        Instance.saveIPConfig();
    }

    public void saveIPConfig() throws Exception
    {
        gson.toJson(this, IOHelper.newWriter(ipConfigFile));
    }

    public List<String> getAllowIp() {
        return allowIp;
    }

    public AuthLimiterIPConfig addAllowIp(String allowIp) {
        this.allowIp.add(allowIp);
        return this;
    }

    public AuthLimiterIPConfig delAllowIp(String allowIp) {
        this.allowIp.removeIf(e -> e.equals(allowIp));
        return this;
    }

    public List<String> getBlockIp() {
        return blockIp;
    }

    public AuthLimiterIPConfig addBlockIp(String blockIp) {
        this.blockIp.add(blockIp);
        return this;
    }

    public AuthLimiterIPConfig delBlockIp(String blockIp) {
        this.blockIp.removeIf(e -> e.equals(blockIp));
        return this;
    }
}