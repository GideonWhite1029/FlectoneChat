package net.flectone.chat.reborn.module.commands;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.manager.FileManager;
import net.flectone.chat.reborn.model.file.FConfiguration;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;

public class MaintenanceListener extends FListener {

    private FileManager fileManager;
    private FConfiguration commands;

    public MaintenanceListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();

        fileManager = FlectoneChatReborn.getPlugin().getFileManager();
        commands = fileManager.getCommands();
    }

    @EventHandler
    public void serverLickPingEvent(@NotNull ServerListPingEvent event) {
        if (!commands.getBoolean("maintenance.turned-on")) return;

        String motd = locale.getVaultString(null, "commands.maintenance.motd.message");
        event.setMotd(MessageUtil.formatAll(null, motd));

        try {
            CachedServerIcon serverIcon = Bukkit.loadServerIcon(fileManager.getIcon("maintenance"));
            event.setServerIcon(serverIcon);
        } catch (Exception e) {
            FlectoneChatReborn.warning("Unable to load maintenance icon");
            e.printStackTrace();
        }

        event.setMaxPlayers(-1);
    }

    @EventHandler
    public void playerLoginEvent(@NotNull PlayerLoginEvent event) {
        if (!commands.getBoolean("maintenance.turned-on")) return;

        Player player = event.getPlayer();
        if (player.hasPermission("flectonechatreborn.commands.maintenance")) return;

        String kickedMessage = locale.getVaultString(player, "commands.maintenance.kicked-message");
        kickedMessage = MessageUtil.formatAll(player, kickedMessage);

        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickedMessage);
    }
}
