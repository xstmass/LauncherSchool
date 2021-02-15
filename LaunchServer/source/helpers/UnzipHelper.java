package launchserver.helpers;

import launcher.helper.IOHelper;
import launcher.helper.LogHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipHelper {
    private static void unpack(URL url, Path dir) throws IOException
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

    private static boolean downloadZip(String link, Path dir) {
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

}
