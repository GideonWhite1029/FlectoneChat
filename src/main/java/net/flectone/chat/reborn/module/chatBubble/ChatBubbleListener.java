package net.flectone.chat.reborn.module.chatBubble;

import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.builder.MessageBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatBubbleListener extends FListener {
    public ChatBubbleListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();
    }

    @EventHandler
    public void playerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled() || event.getRecipients().isEmpty()) return;

        Player player = event.getPlayer();
        if (!getModule().isEnabledFor(player)) return;
        if (hasNoPermission(player)) return;

        String message = event.getMessage();

        MessageBuilder messageBuilder = new MessageBuilder(player, player.getInventory().getItemInMainHand(), message, new ArrayList<>());
        ((ChatBubbleModule) getModule()).add(player, messageBuilder.getMessage(""));
    }
}
