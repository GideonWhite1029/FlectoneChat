package net.flectone.chat.reborn.module.extra.mark;

import net.flectone.chat.reborn.FlectoneChatReborn;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.flectone.chat.reborn.model.mark.Mark.COLOR_VALUES;

public class MarkListener extends FListener {

    public MarkListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();
    }

    @EventHandler
    public void playerJoinEvent(@NotNull PlayerJoinEvent event) {
        ((MarkModule) getModule()).removeBugEntities(event.getPlayer());
    }

    @EventHandler
    public void markEvent(@NotNull PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getItemMeta() == null) return;

        Player player = event.getPlayer();
        if (!config.getVaultBoolean(player, getModule() + ".enable")) return;
        if (!getModule().isEnabledFor(player)) return;
        if (hasNoPermission(player)) return;

        Material triggerMaterial;

        try {
            triggerMaterial = Material.valueOf(config.getVaultString(player, getModule() + ".item").toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            FlectoneChatReborn.warning("Item for mark was not found");
            triggerMaterial = Material.WOODEN_SWORD;
        }

        if (!itemStack.getType().equals(triggerMaterial)) return;

        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        if (fPlayer.isMuted()) {
            fPlayer.sendMutedMessage("mark");
            return;
        }

        if (fPlayer.isHaveCooldown(getModule().toString())) {
            fPlayer.sendCDMessage("mark", "mark");
            return;
        }

        fPlayer.playSound(getModule().toString());

        String itemName = itemStack.getItemMeta().getDisplayName().toUpperCase();
        String color = COLOR_VALUES.contains(itemName)
                ? itemName
                : "WHITE";

        int range = config.getVaultInt(player,getModule() + ".range");

        ((MarkModule) getModule()).mark(player, range, color);
    }
}
