package net.flectone.chat.reborn.module.player.world;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.model.player.FPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class WorldListener extends FListener {

    public WorldListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();

        Bukkit.getScheduler().runTaskLater(FlectoneChatReborn.getPlugin(), () ->
                Bukkit.getOnlinePlayers().forEach(this::setWorld), 20L);
    }

    @EventHandler
    public void playerJoinEvent(@NotNull PlayerJoinEvent event) {
        setWorld(event.getPlayer());
    }

    @EventHandler
    public void playerChangeWorldEvent(@NotNull PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (hasNoPermission(player)) return;

        setWorld(player);
    }

    public void setWorld(@NotNull Player player) {
        World world = player.getWorld();

        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        fPlayer.setWorldPrefix(((WorldModule) getModule()).getPrefix(player, world));
        fPlayer.updateTeam();
    }
}
