package launchserver.plugin.velocity;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

public class CommandVelocity {
    private CommandVelocity() {}

    // TODO: разобраться с прокидыванием комманд
    public static void launchserver(PluginVelocity plugin, CommandManager manager) {

        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder.<CommandSource>literal("launchservervelocity")
                .requires(src -> src.hasPermission("launchservervelocity.admin"))
                .build();

        BrigadierCommand brigadier = new BrigadierCommand(command);

        CommandMeta meta = manager.metaBuilder(brigadier)
                .plugin(plugin)
                .aliases("launchservervelocity", "launchervelocity", "lsv")
                .build();

        manager.register(meta, brigadier);
    }
}
