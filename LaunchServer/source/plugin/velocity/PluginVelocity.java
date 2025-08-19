package launchserver.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import launcher.helper.CommonHelper;
import launchserver.plugin.PluginBridge;
import launchserver.plugin.bungee.CommandBungee;
import org.slf4j.Logger;

import java.nio.file.Path;

public final class PluginVelocity {

    public volatile PluginBridge bridge = null;
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path pluginDirectory;

    @Inject
    public PluginVelocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.pluginDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Initialize LaunchServer
        try {
            bridge = new PluginBridge(pluginDirectory);
        } catch (Throwable exc) {
            exc.printStackTrace();
        }

        CommonHelper.newThread("LaunchServer Thread", true, bridge).start();
        CommandVelocity.launchserver(this, proxy.getCommandManager());
    }
}
