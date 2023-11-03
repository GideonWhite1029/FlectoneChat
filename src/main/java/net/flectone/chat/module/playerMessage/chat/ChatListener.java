package net.flectone.chat.module.playerMessage.chat;

import net.flectone.chat.manager.FPlayerManager;
import net.flectone.chat.model.player.FPlayer;
import net.flectone.chat.model.player.Moderation;
import net.flectone.chat.module.FListener;
import net.flectone.chat.module.FModule;
import net.flectone.chat.module.commands.SpyListener;
import net.flectone.chat.util.MessageUtil;
import net.flectone.chat.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.flectone.chat.manager.FileManager.config;
import static net.flectone.chat.manager.FileManager.locale;

public class ChatListener extends FListener {
    public ChatListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        register();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player sender = event.getPlayer();
        FPlayer fPlayer = FPlayerManager.get(sender);
        if (fPlayer == null) return;

        if (fPlayer.isMuted()) {

            String message = locale.getVaultString(fPlayer.getPlayer(), "commands.muted");

            Moderation mute = fPlayer.getMute();
            message = message
                    .replace("<time>", TimeUtil.convertTime(fPlayer.getPlayer(), mute.getTime() - TimeUtil.getCurrentTime()))
                    .replace("<reason>", mute.getReason())
                    .replace("<moderator>", mute.getModeratorName());

            message = MessageUtil.formatAll(fPlayer.getPlayer(), message);

            sender.sendMessage(message);

            event.setCancelled(true);
            return;
        }

        String message = event.getMessage();

        List<String> availableChatsList = config.getCustomList(sender, getModule() + ".list");

        String playerChat = fPlayer.getSettings().getChat();
        if (playerChat == null || !availableChatsList.contains(playerChat)) {

            int priority = -1;

            for (String chatType : availableChatsList) {
                String chatTrigger = config.getVaultString(sender, getModule() + ".list." + chatType + ".prefix.trigger");
                if (!chatTrigger.isEmpty() && !message.startsWith(chatTrigger)) continue;

                int chatPriority = config.getVaultInt(sender, getModule() + ".list." + chatType + ".priority");
                if (chatPriority <= priority) continue;

                if (hasNoPermission(sender, chatType)) continue;

                playerChat = chatType;
            }
        }

        if (playerChat == null) {
            String chatNotFound = locale.getVaultString(sender, getModule() + ".not-found");
            sender.sendMessage(MessageUtil.formatAll(sender, chatNotFound));

            event.setCancelled(true);
            return;
        }

        SpyListener.send(sender, playerChat, event.getMessage());

        boolean prefixIsCleared = config.getVaultBoolean(sender, getModule() + ".list." + playerChat + ".prefix.cleared");
        if (prefixIsCleared) {
            String chatTrigger = config.getVaultString(sender, getModule() + ".list." + playerChat + ".prefix.trigger");
            if (!chatTrigger.isEmpty() && message.startsWith(chatTrigger)) {
                message = message.replaceFirst(chatTrigger, "");
            }
        }

        List<Player> recipientsList = new ArrayList<>(Bukkit.getOnlinePlayers());

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
            FPlayer fRecipient = FPlayerManager.get(player);
            return fRecipient != null && fRecipient.getIgnoreList().contains(fPlayer.getUuid());
        });

        String chatFormat = config.getVaultString(sender, getModule() + ".list." + playerChat + ".format");
        chatFormat = MessageUtil.formatPlayerString(sender, chatFormat);

        List<String> featuresList = config.getVaultStringList(sender, getModule() + ".list." + playerChat + ".features");
        ((ChatModule) getModule()).send(sender, recipientsList, message, chatFormat, featuresList);

        boolean noRecipientsMessageEnabled = config.getVaultBoolean(sender, getModule() + ".list." + playerChat + ".no-recipients.enable");
        if ((recipientsList.isEmpty() || recipientsList.size() == 1) && noRecipientsMessageEnabled) {
            String recipientsEmpty = locale.getVaultString(sender, getModule() + ".no-recipients");
            sender.sendMessage(MessageUtil.formatAll(sender, recipientsEmpty));
        }

        boolean isCancelled = config.getVaultBoolean(sender, getModule() + ".list." + playerChat + ".set-cancelled");
        event.setCancelled(isCancelled);
        event.getRecipients().clear();
    }
}
