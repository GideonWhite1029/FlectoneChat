package net.flectone.chat.reborn.module.playerMessage.chat;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.builder.MessageBuilder;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.chatBubble.ChatBubbleModule;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatModule extends FModule {

    public ChatModule(FModule module, String name) {
        super(module, name);
        init();
    }

    @Override
    public void init() {
        if (!isEnabled()) return;
        register();

        actionManager.add(new ChatListener(this));
    }

    public void send(@NotNull Player sender, @NotNull List<Player> recipientsList,
                     @NotNull String message, @NotNull String chatFormat,
                     @NotNull List<String> featuresList, @NotNull Runnable noRecipientRunnable) {

        message = IntegrationsModule.interactiveChatMark(message, sender.getUniqueId());

        @NotNull String finalMessage = message;
        Bukkit.getScheduler().runTaskAsynchronously(FlectoneChatReborn.getPlugin(), () -> {
            MessageBuilder messageBuilder = new MessageBuilder(sender, sender.getInventory().getItemInMainHand(), finalMessage, featuresList, true);

            recipientsList.forEach(player ->
                    player.spigot().sendMessage(messageBuilder.buildFormat(sender, player, chatFormat, true)));

            FModule fModule = moduleManager.get(ChatBubbleModule.class);
            if (fModule instanceof ChatBubbleModule chatBubbleModule) {
                chatBubbleModule.add(sender, messageBuilder.getMessage(""));
            }

            noRecipientRunnable.run();
        });
    }
}
