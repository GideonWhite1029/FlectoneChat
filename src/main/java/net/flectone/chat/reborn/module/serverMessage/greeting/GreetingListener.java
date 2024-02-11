package net.flectone.chat.reborn.module.serverMessage.greeting;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class GreetingListener extends FListener {

    public GreetingListener(FModule module) {
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

        Bukkit.getScheduler().runTaskAsynchronously(FlectoneChatReborn.getPlugin(), () ->
                ((GreetingModule) getModule()).send(player));
    }
}
