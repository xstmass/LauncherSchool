package launchserver.command.hash;

import launcher.helper.IOHelper;
import launcher.helper.LogHelper;
import launcher.serialize.config.entry.StringConfigEntry;
import launchserver.LaunchServer;
import launchserver.command.Command;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class DownloadAssetCommand extends Command
{
    private static String ASSET_URL_MASK;

    public DownloadAssetCommand(LaunchServer server)
    {
        super(server);
    }

    public static void unpack(URL url, Path dir) throws IOException
    {
        try (ZipInputStream input = IOHelper.newZipInput(url))
        {
            for (ZipEntry entry = input.getNextEntry(); entry != null; entry = input.getNextEntry())
            {
                if (entry.isDirectory())
                {
                    continue; // Skip directories
                }

                // Unpack entry
                String name = entry.getName();
                LogHelper.subInfo("Downloading file: '%s'", name);
                IOHelper.transfer(input, dir.resolve(IOHelper.toPath(name)));
            }
        }
    }

    public static void downloadZip(String[] mirrors, String mask, Path dir) {
        for (String mirror : mirrors) {
            if (downloadZip(mirror + mask, dir)) return;
        }
        LogHelper.error("Error download %s. All mirrors return error", dir.getFileName().toString());
    }

    public static boolean downloadZip(String link, Path dir) {
        URL url = null;
        // Нам тут IDEA мозг ебет
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        LogHelper.debug("Try download %s", url.toString());
        try {
            unpack(url, dir);
        } catch (IOException e) {
            LogHelper.error("Download %s failed (%s: %s)", url.toString(), e.getClass().getName(), e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String getArgsDescription()
    {
        return "<version> <dir>";
    }

    @Override
    public String getUsageDescription()
    {
        return "Download asset dir";
    }

    @Override
    public void invoke(String... args) throws Throwable
    {
        verifyArgs(args, 2);
        String version = args[0];
        String dirName = IOHelper.verifyFileName(args[1]);
        Path assetDir = server.updatesDir.resolve(dirName);

        // Create asset dir
        LogHelper.subInfo("Creating asset dir: '%s'", dirName);
        Files.createDirectory(assetDir);

        // Download required asset
        LogHelper.subInfo("Downloading asset, it may take some time");
        ASSET_URL_MASK = String.format("assets/%s.zip", version);
        String[] mirrors = server.config.mirrors.stream(StringConfigEntry.class).toArray(String[]::new);
        downloadZip(mirrors, ASSET_URL_MASK, assetDir);

        // Finished
        server.syncUpdatesDir(Collections.singleton(dirName));
        LogHelper.subInfo("Asset successfully downloaded: '%s'", dirName);
    }
}
