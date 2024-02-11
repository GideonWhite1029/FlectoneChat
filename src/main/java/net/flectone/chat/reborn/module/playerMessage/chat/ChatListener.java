package net.flectone.chat.reborn.module.playerMessage.chat;

import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.commands.CommandSpy;
import net.flectone.chat.reborn.module.integrations.IntegrationsModule;
import net.flectone.chat.reborn.util.MessageUtil;
import net.flectone.chat.reborn.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatListener extends FListener {

    public ChatListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();
    }

    @EventHandler
    public void playerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        if (event.getRecipients().isEmpty()) return;
        // ULTRA MEGA SUPER 200 IQ MOVE FOR ULTRA MEGA SUPER 200 IQ CHAT-PLUGINS
        if (!event.getFormat().equals("<%1$s> %2$s")) return;

        Player sender = event.getPlayer();
        FPlayer fPlayer = playerManager.get(sender);
        if (fPlayer == null) return;
        if (!getModule().isEnabledFor(sender)) return;

        String message = event.getMessage();

        List<String> availableChatsList = config.getCustomList(sender, getModule() + ".list");

        String playerChat = fPlayer.getSettings().getChat();
        if (playerChat == null || !availableChatsList.contains(playerChat)) {

            int priority = Integer.MIN_VALUE;

            for (String chatType : availableChatsList) {
                if (!config.getVaultBoolean(sender, getModule() + ".list." + chatType + ".enable")) continue;
                String chatTrigger = config.getVaultString(sender, getModule() + ".list." + chatType + ".prefix.trigger");
                if (!chatTrigger.isEmpty() && !message.startsWith(chatTrigger)) continue;
                if (message.equals(chatTrigger)) continue;

                int chatPriority = config.getVaultInt(sender, getModule() + ".list." + chatType + ".priority");
                if (chatPriority <= priority) continue;
                if (hasNoPermission(sender, chatType)) continue;

                playerChat = chatType;
                priority = chatPriority;
            }
        }

        if (playerChat == null || !sender.hasPermission(getPermission())) {
            String chatNotFound = locale.getVaultString(sender, getModule() + ".not-found");
            sender.sendMessage(MessageUtil.formatAll(sender, chatNotFound));

            event.setCancelled(true);
            return;
        }

        if (fPlayer.isMuted()) {
            fPlayer.sendMutedMessage(playerChat);
            event.setCancelled(true);
            return;
        }

        if (fPlayer.isHaveCooldown(getModule() + "." + playerChat)) {
            fPlayer.sendCDMessage(playerChat, playerChat);
            event.setCancelled(true);
            return;
        }

        List<String> featuresList = config.getVaultStringList(sender, getModule() + ".list." + playerChat + ".features");
        if (featuresList.contains("mention")) {
            message = IntegrationsModule.interactiveChatCheckMention(event);
        }

        boolean prefixIsCleared = config.getVaultBoolean(sender, getModule() + ".list." + playerChat + ".prefix.cleared");
        if (prefixIsCleared) {
            String chatTrigger = config.getVaultString(sender, getModule() + ".list." + playerChat + ".prefix.trigger");
            if (!chatTrigger.isEmpty() && message.startsWith(chatTrigger)) {
                message = message.replaceFirst(chatTrigger, "");
                if (message.startsWith(" ")) message = message.replaceFirst(" ", "");
            }
        }

        List<Player> recipientsList = (List<Player>) PlayerUtil.getPlayersWithFeature(getModule() + ".enable");
        recipientsList = (List<Player>) PlayerUtil.getPlayersWithFeature(recipientsList, getModule() + ".list." + playerChat + ".enable");

        String finalPlayerChat = playerChat;
        recipientsList.removeIf(player -> !player.hasPermission(getPermission())
                || !player.hasPermission(getPermission() + "." + finalPlayerChat));

        List<String> chatWorldsList = config.getVaultStringList(sender, getModule() + ".list." + playerChat + ".worlds");
        if (!chatWorldsList.isEmpty()) {
            List<World> enableWorldsList = new ArrayList<>();
            for (String worldName : chatWorldsList) {
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;
                enableWorldsList.add(world);
            }

            recipientsList.removeIf(player -> !enableWorldsList.contains(player.getWorld()));
        }

        int maxRange = config.getVaultInt(sender, getModule() + ".list." + playerChat + ".range");
        if (maxRange != 0) {
            recipientsList.removeIf(player -> sender.getWorld() != player.getWorld()
                    || sender.getLocation().distance(player.getLocation()) > maxRange);
        }

        recipientsList.removeIf(player -> {
            FPlayer fRecipient = playerManager.get(player);
            return fRecipient != null && fRecipient.getIgnoreList().contains(fPlayer.getUuid());
        });

        String chatFormat = config.getVaultString(sender, getModule() + ".list." + playerChat + ".format");
        chatFormat = MessageUtil.formatPlayerString(sender, chatFormat);

        List<Player> finalRecipientsList = recipientsList;
        String finalPlayerChat1 = playerChat;
        ((ChatModule) getModule()).send(sender, recipientsList, message, chatFormat, featuresList, () -> {
            finalRecipientsList.removeIf(player -> player.getGameMode() == GameMode.SPECTATOR);

            boolean noRecipientsMessageEnabled = config.getVaultBoolean(sender, getModule() + ".list." + finalPlayerChat1 + ".no-recipients.enable");
            if ((finalRecipientsList.isEmpty() || finalRecipientsList.size() == 1) && noRecipientsMessageEnabled) {
                String recipientsEmpty = locale.getVaultString(sender, getModule() + ".no-recipients");
                sender.sendMessage(MessageUtil.formatAll(sender, recipientsEmpty));
            }
        });

        CommandSpy.send(sender, playerChat, recipientsList, CommandSpy.Type.DEFAULT, message);

        fPlayer.playSound(sender, recipientsList, getModule() + "." + playerChat);

        boolean isCancelled = config.getVaultBoolean(sender, getModule() + ".list." + playerChat + ".set-cancelled");
        event.setCancelled(isCancelled);
        event.setMessage(message);
        event.getRecipients().clear();
    }
}
