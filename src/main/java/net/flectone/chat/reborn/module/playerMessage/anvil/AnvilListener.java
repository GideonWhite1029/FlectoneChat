package net.flectone.chat.reborn.module.playerMessage.anvil;

import net.flectone.chat.reborn.builder.MessageBuilder;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.module.commands.CommandSpy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AnvilListener extends FListener {

    public AnvilListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();
    }

    @EventHandler
    public void anvilEvent(@NotNull InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof AnvilInventory)
                || event.getSlot() != 2
                || event.getCurrentItem() == null
                || event.getCurrentItem().getItemMeta() == null
                || !(event.getWhoClicked() instanceof Player player)) return;

        if (event.isCancelled()) return;
        if (!getModule().isEnabledFor(player)) return;
        if (hasNoPermission(player)) return;

        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        if (fPlayer.isMuted()) {
            fPlayer.sendMutedMessage("anvil");
            event.setCancelled(true);
        }

        if (fPlayer.isHaveCooldown(getModule().toString())) {
            fPlayer.sendCDMessage("anvil", "anvil");
            event.setCancelled(true);
            return;
        }

        fPlayer.playSound(getModule().toString());

        ItemStack itemStack = event.getCurrentItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        String displayName = itemMeta.getDisplayName();
        if (displayName.isEmpty()) return;

        List<String> features = config.getVaultStringList(player, getModule() + ".features");
        MessageBuilder messageBuilder = new MessageBuilder(player, itemStack, displayName, features);
        displayName = messageBuilder.getMessage(String.valueOf(ChatColor.ITALIC));

        if (ChatColor.stripColor(displayName).isEmpty()) return;

        CommandSpy.send(player, "anvil", new ArrayList<>(), CommandSpy.Type.DEFAULT, displayName);

        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
    }
}
