package launchserver.auth.limiter;

import launcher.LauncherAPI;
import launcher.helper.VerifyHelper;
import launcher.serialize.config.ConfigObject;
import launcher.serialize.config.entry.*;
import launchserver.LaunchServer;

public class AuthLimiterConfig extends ConfigObject
{
    @LauncherAPI
    public LaunchServer server;
    @LauncherAPI
    public int authRateLimit;
    @LauncherAPI
    public int authRateLimitMilis;
    @LauncherAPI
    public String authRejectString;
    @LauncherAPI
    public String authBannedString;
    @LauncherAPI
    public boolean blockOnConnect;
    @LauncherAPI
    public ListConfigEntry allowIp;
    @LauncherAPI
    public ListConfigEntry blockIp;

    @LauncherAPI
    public AuthLimiterConfig(BlockConfigEntry block)
    {
        super(block);
        authRateLimit = VerifyHelper.verifyInt(block.getEntryValue("authRateLimit", IntegerConfigEntry.class),
                VerifyHelper.range(0, 1000000), "Illegal authRateLimit");
        authRateLimitMilis = VerifyHelper.verifyInt(block.getEntryValue("authRateLimitMilis", IntegerConfigEntry.class),
                VerifyHelper.range(10, 10000000), "Illegal authRateLimitMillis");
        authRejectString = block.hasEntry("authRejectString") ?
                block.getEntryValue("authRejectString", StringConfigEntry.class) : "Превышен лимит авторизаций. Подождите некоторое время перед повторной попыткой";
        authBannedString = block.hasEntry("authBannedString") ?
                block.getEntryValue("authBannedString", StringConfigEntry.class) : "Ваш IP заблокирован!";
        blockOnConnect = block.getEntryValue("blockOnConnect", BooleanConfigEntry.class);
        allowIp = block.getEntry("allowIp", ListConfigEntry.class);
        blockIp = block.getEntry("blockIp", ListConfigEntry.class);
    }
}
