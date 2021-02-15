package launchserver.command.hash;

import launcher.helper.IOHelper;
import launcher.helper.LogHelper;
import launcher.serialize.config.entry.StringConfigEntry;
import launchserver.LaunchServer;
import launchserver.command.Command;
import launchserver.helpers.UnzipHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public final class DownloadAssetCommand extends Command
{
    public DownloadAssetCommand(LaunchServer server)
    {
        super(server);
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
        String[] mirrors = server.config.mirrors.stream(StringConfigEntry.class).toArray(String[]::new);
        String assetMask = String.format("assets/%s.zip", version);
        UnzipHelper.downloadZip(mirrors, assetMask, assetDir);

        // Finished
        server.syncUpdatesDir(Collections.singleton(dirName));
        LogHelper.subInfo("Asset successfully downloaded: '%s'", dirName);
    }
}
