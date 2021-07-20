package launchserver.auth.limiter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import launcher.helper.LogHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class AuthLimiterIPConfig
{
    public static File ipConfigFile;
    public static Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    public static AuthLimiterIPConfig Instance;

    @Expose
    List<String> allowIp = new ArrayList<>();
    @Expose
    List<String> blockIp = new ArrayList<>();

    public static void load(File file) throws Exception {
        ipConfigFile = file;
        if (file.exists()) {
            LogHelper.subDebug("IP List file found! Loading...");
            if (file.length() > 2) {
                try
                {
                    AuthLimiterIPConfig authLimiterIPConfig = gson.fromJson(new JsonReader(new FileReader(file)), AuthLimiterIPConfig.class);
                    Instance = authLimiterIPConfig;
                    return;
                }
                catch (FileNotFoundException error)
                {
                    LogHelper.subWarning("Ip List not reading!");
                    if (LogHelper.isDebugEnabled()) LogHelper.error(error);
                }
            }
        }

        LogHelper.subWarning("IP List file not found! Creating file...");
        AuthLimiterIPConfig IpConfig = new AuthLimiterIPConfig();
        Instance = IpConfig;
        IpConfig.saveIPConfig();
    }

    public void saveIPConfig() throws Exception
    {
        if (!ipConfigFile.exists()) ipConfigFile.createNewFile();

        FileWriter fw = new FileWriter(ipConfigFile, false);
        fw.write(gson.toJson(this));
        fw.close();
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