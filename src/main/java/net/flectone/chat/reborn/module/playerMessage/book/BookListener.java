package net.flectone.chat.reborn.module.playerMessage.book;


import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.builder.MessageBuilder;
import net.flectone.chat.reborn.manager.FPlayerManager;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.commands.CommandSpy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BookListener extends FListener {

    private FPlayerManager playerManager;

    public BookListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();

        playerManager = FlectoneChatReborn.getPlugin().getPlayerManager();
    }

    @EventHandler
    public void bookEvent(@NotNull PlayerEditBookEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (!getModule().isEnabledFor(player)) return;
        if (hasNoPermission(player)) return;

        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        if (fPlayer.isMuted()) {
            fPlayer.sendMutedMessage("book");
            event.setCancelled(true);
        }

        if (fPlayer.isHaveCooldown(getModule().toString())) {
            fPlayer.sendCDMessage("book", "book");
            event.setCancelled(true);
            return;
        }

        fPlayer.playSound(getModule().toString());

        BookMeta bookMeta = event.getNewBookMeta();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        List<String> features = config.getVaultStringList(player, getModule() + ".features");

        for (int x = 1; x <= event.getNewBookMeta().getPages().size(); x++) {
            String string = bookMeta.getPage(x);

            if (string.isEmpty()) continue;

            MessageBuilder messageBuilder = new MessageBuilder(player, itemInHand, string, features);
            bookMeta.setPage(x, messageBuilder.getMessage(""));
            CommandSpy.send(player, "book", new ArrayList<>(), CommandSpy.Type.DEFAULT, messageBuilder.getMessage(""));
        }

        if (event.isSigning() && bookMeta.getTitle() != null) {
            MessageBuilder messageBuilder = new MessageBuilder(player, itemInHand, bookMeta.getTitle(), features);
            bookMeta.setTitle(messageBuilder.getMessage(""));
            CommandSpy.send(player, "book", new ArrayList<>(), CommandSpy.Type.DEFAULT, messageBuilder.getMessage(""));
        }

        event.setNewBookMeta(bookMeta);
    }
}
