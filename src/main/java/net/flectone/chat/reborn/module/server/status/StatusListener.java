package net.flectone.chat.reborn.module.server.status;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.util.MessageUtil;
import net.flectone.chat.reborn.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.List;

public class StatusListener extends FListener {

    public StatusListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();
    }

    @EventHandler
    public void statusEvent(@NotNull ServerListPingEvent event) {
        if (commands.getBoolean("maintenance.turned-on")) return;

        if (config.getBoolean("default." + getModule() + ".motd.enable")) {
            List<String> motds = locale.getStringList("default." + getModule() + ".motd.message");

            if (!motds.isEmpty()) {
                int numberMotd = RandomUtil.nextInt(0, motds.size());
                String motd = MessageUtil.formatAll(null, motds.get(numberMotd));
                event.setMotd(motd);
            }
        }

        if (config.getBoolean("default." + getModule() + ".player-count.enable")) {
            event.setMaxPlayers(config.getInt("default." + getModule() + ".player-count.number"));
        }

        if (config.getBoolean("default." + getModule() + ".icon.enable")) {
            List<String> iconNames = config.getStringList("default." + getModule() + ".icon.names");
            if (!iconNames.isEmpty()) {
                int numberIcon = config.getString("default." + getModule() + ".icon.mode").equals("single")
                        ? 0
                        : RandomUtil.nextInt(0, iconNames.size());

                setIcon(event, iconNames.get(numberIcon));
            }
        }
    }

    @EventHandler
    public void playerLoginEvent(@NotNull PlayerLoginEvent event) {
        if (!config.getBoolean("default." + getModule() + ".player-count.enable")) return;

        Player player = event.getPlayer();

        int playerCounts = Bukkit.getOnlinePlayers().size();
        int maxCounts = config.getInt("default." + getModule() + ".player-count.number");

        if (playerCounts < maxCounts || player.hasPermission(getPermission() + ".player-count.bypass")) return;

        String kickMessage = locale.getVaultString(player, getModule() + ".player-count.server-full");
        kickMessage = MessageUtil.formatAll(player, kickMessage);

        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessage);
    }

    public void setIcon(@NotNull ServerListPingEvent event, @NotNull String iconName) {
        try {

            BufferedImage bufferedImage = FlectoneChatReborn.getPlugin().getFileManager().getIcon(iconName);
            if (bufferedImage.getWidth() != 64 || bufferedImage.getHeight() != 64) {
                FlectoneChatReborn.warning("Unable to set icon " + iconName + ".png, size should be 64x64");
                return;
            }

            CachedServerIcon serverIcon = Bukkit.loadServerIcon(bufferedImage);
            event.setServerIcon(serverIcon);
        } catch (Exception exception) {
            FlectoneChatReborn.warning("Unable to load and install " + iconName + ".png image");
            exception.printStackTrace();
        }
    }
}
