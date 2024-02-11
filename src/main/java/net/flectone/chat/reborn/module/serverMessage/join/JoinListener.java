package net.flectone.chat.reborn.module.serverMessage.join;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class JoinListener extends FListener {
    public JoinListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();
    }

    @EventHandler
    public void playerJoinEvent(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (hasNoPermission(player)) return;
        if (IntegrationsModule.isVanished(player)) return;

        boolean hasPlayerBefore = player.hasPlayedBefore() || !config.getVaultBoolean(player, getModule() + ".first-time.enable");

        String string = hasPlayerBefore
                ? locale.getVaultString(player, getModule() + ".message")
                : locale.getVaultString(player, getModule() + ".first-time.message");

        FlectoneChatReborn.getPlugin().getDatabase().execute(() -> ((JoinModule) getModule()).sendAll(player, string));

        IntegrationsModule.sendDiscordJoin(player, hasPlayerBefore ? "usually" : "first");

        event.setJoinMessage(null);
    }
}
