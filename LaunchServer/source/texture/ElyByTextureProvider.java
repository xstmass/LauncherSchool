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
        return cacheTextureProvider.getCached(uuid, username, "https://authserver.ely.by/api/users/profiles/minecraft/", "https://skinsystem.ely.by/profile/", "ElyBy").skin;
    }

    @Override
    public synchronized Texture getCloakTexture(UUID uuid, String username)
    {
        return cacheTextureProvider.getCached(uuid, username, "https://authserver.ely.by/api/users/profiles/minecraft/", "https://skinsystem.ely.by/profile/", "ElyBy").cloak;
    }
}