package launchserver.texture;

import launcher.client.PlayerProfile.Texture;
import launcher.serialize.config.entry.BlockConfigEntry;

import java.util.UUID;

public class ElyByTextureProvider extends TextureProvider
{
    protected CacheTextureProvider cacheTextureProvider;

    public ElyByTextureProvider(BlockConfigEntry block)
    {
        super(block);
    }

    @Override
    public void close()
    {
        // Do nothing
    }

    @Override
    public synchronized Texture getSkinTexture(UUID uuid, String username)
    {
        return cacheTextureProvider.getCached(uuid, username, "http://skinsystem.ely.by/profile/", "https://sessionserver.minesocial.net/session/minecraft/profile/", "ElyBy").skin;
    }

    @Override
    public synchronized Texture getCloakTexture(UUID uuid, String username)
    {
        return cacheTextureProvider.getCached(uuid, username, "http://skinsystem.ely.by/profile/", "https://sessionserver.minesocial.net/session/minecraft/profile/", "ElyBy").cloak;
    }
}
