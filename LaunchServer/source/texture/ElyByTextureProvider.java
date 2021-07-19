package launchserver.texture;

import launcher.client.PlayerProfile.Texture;
import launcher.helper.LogHelper;
import launcher.serialize.config.entry.BlockConfigEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class ElyByTextureProvider extends TextureProvider
{
    public ElyByTextureProvider(BlockConfigEntry block)
    {
        super(block);
    }

    private static Texture getTexture(String url, boolean cloak) throws IOException
    {
        LogHelper.debug("Getting texture: '%s'", url);
        try
        {
            return new Texture(url, cloak);
        }
        catch (FileNotFoundException ignored)
        {
            if (LogHelper.isDebugEnabled()) LogHelper.subDebug("Texture not set or not found! Maybe problem with you proxy!");
            return null; // Simply not found
        }
    }

    @Override
    public void close()
    {
        // Do nothing
    }

    @Override
    public Texture getCloakTexture(UUID uuid, String username) throws IOException
    {
        return getTexture(String.format("http://skinsystem.ely.by/cloaks/%s.png", username), true);
    }

    @Override
    public Texture getSkinTexture(UUID uuid, String username) throws IOException
    {
        return getTexture(String.format("http://skinsystem.ely.by/skins/%s.png", username), false);
    }
}
