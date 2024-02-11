package net.flectone.chat.reborn.module.extra.itemSign;

import net.flectone.chat.reborn.module.FListener;
import net.flectone.chat.reborn.module.FModule;
import net.flectone.chat.reborn.model.player.FPlayer;
import net.flectone.chat.reborn.util.MessageUtil;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class ItemSignListener extends FListener {

    public ItemSignListener(FModule module) {
        super(module);
        init();
    }

    @Override
    public void init() {
        registerEvents();
    }

    @EventHandler
    public void itemSignEvent(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (clickedBlock.getLocation().getWorld() == null) return;

        Player player = event.getPlayer();
        if (!config.getVaultBoolean(player, getModule() + ".enable")) return;
        if (!getModule().isEnabledFor(player)) return;
        if (hasNoPermission(player)) return;

        String configBlock = config.getVaultString(player, getModule() + ".block");
        if (!clickedBlock.getType().toString().equalsIgnoreCase(configBlock)) return;

        FPlayer fPlayer = playerManager.get(player);
        if (fPlayer == null) return;

        if (fPlayer.isMuted()) {
            fPlayer.sendMutedMessage("item-sign");
            return;
        }

        if (fPlayer.isHaveCooldown(getModule().toString())) {
            fPlayer.sendCDMessage("item-sign", "item-sign");
            return;
        }

        String signFormat = config.getVaultString(player, getModule() + ".format");

        signFormat = MessageUtil.formatPlayerString(player, signFormat);

        boolean dropDyeEnabled = config.getVaultBoolean(player, getModule() + ".unsign.drop-dye");
        boolean isCompleted = ((ItemSignModule) getModule()).sign(player, clickedBlock.getLocation(), player.getInventory(),
                signFormat, dropDyeEnabled);

        event.setCancelled(isCompleted);

        if (isCompleted) fPlayer.playSound(getModule().toString());
    }
}
